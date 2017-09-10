(ns re-frame-datatable.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [trim-v]]
            [cljs.spec.alpha :as s]))


; --- Model (spec) ---

(s/def ::db-id keyword?)
(s/def ::enabled? boolean?)
(s/def ::css-classes (s/coll-of string?))


; columns-def

(s/def ::column-key (s/coll-of #(or (keyword? %) (int? %)) :kind vector))
(s/def ::column-label (s/or :string string?
                            :component fn?))
(s/def ::comp-fn fn?)
(s/def ::sorting (s/keys :req [::enabled?]
                         :opt [::comp-fn]))
(s/def ::td-class-fn fn?)


(s/def ::column-def
  (s/keys :req [::column-key]
          :opt [::sorting ::render-fn ::td-class-fn ::column-label]))

(s/def ::columns-def (s/coll-of ::column-def :min-count 1))


; options

(s/def ::table-classes ::css-classes)

(s/def ::per-page (s/and integer? pos?))
(s/def ::cur-page (s/and integer? (complement neg?)))
(s/def ::total-pages (s/and integer? pos?))
(s/def ::pagination
  (s/keys :req [::enabled?]
          :opt [::per-page ::cur-page ::total-pages]))

(s/def ::selected-indexes (s/coll-of nat-int? :kind set))
(s/def ::selection
  (s/keys :req [::enabled?]
          :opt [::selected-indexes]))

(s/def ::extra-header-row-component fn?)
(s/def ::footer-component fn?)
(s/def ::header-enabled? ::enabled?)

(s/def ::options
  (s/nilable
    (s/keys :opt [::pagination
                  ::header-enabled?
                  ::table-classes
                  ::selection
                  ::extra-header-row-component
                  ::footer-component])))


; --- Re-frame database paths ---

(def root-db-path [::re-frame-datatable])
(defn db-path-for [db-path db-id]
  (vec (concat (conj root-db-path db-id)
               db-path)))

(def columns-def-db-path (partial db-path-for [::columns-def]))
(def options-db-path (partial db-path-for [::options]))
(def state-db-path (partial db-path-for [::state]))
(def sort-key-db-path (partial db-path-for [::state ::sort ::sort-key]))
(def sort-comp-order-db-path (partial db-path-for [::state ::sort ::sort-comp]))
(def sort-comp-fn-db-path (partial db-path-for [::state ::sort ::sort-fn]))


; --- Defaults ---

(def default-per-page 25)


; --- Utils ---
(defn css-class-str [classes]
  {:class (->> classes
               (filter (complement nil?))
               (clojure.string/join \space))})



; --- Events ---
; ----------------------------------------------------------------------------------------------------------------------

(re-frame/reg-event-db
  ::on-will-mount
  [trim-v]
  (fn [db [db-id data-sub columns-def options]]
    (-> db
        (assoc-in (columns-def-db-path db-id)
                  columns-def)
        (assoc-in (options-db-path db-id)
                  options)
        (assoc-in (state-db-path db-id)
                  {::pagination  (merge {::per-page default-per-page
                                         ::cur-page 0}
                                        (select-keys (::pagination options) [::per-page ::enabled?]))
                   ::total-items (count @(re-frame/subscribe data-sub))
                   ::selection   (merge {::selected-indexes (if (get-in options [::selection ::enabled?])
                                                              (or (get-in options [::selection ::selected-indexes]) #{})
                                                              #{})}
                                        (select-keys (::selection options) [::enabled?]))}))))


(re-frame/reg-event-db
  ::on-did-update
  [trim-v]
  (fn [db [db-id data-sub columns-def options]]
    (-> db
        (assoc-in (columns-def-db-path db-id)
                  columns-def)
        (assoc-in (options-db-path db-id)
                  options)

        (assoc-in (conj (state-db-path db-id) ::total-items) (count @(re-frame/subscribe data-sub))))))


(re-frame/reg-event-db
  ::on-will-unmount
  [trim-v]
  (fn [db [db-id]]
    (update-in db root-db-path dissoc db-id)))



(re-frame/reg-event-db
  ::change-state-value
  [trim-v]
  (fn [db [db-id state-path new-val]]
    (assoc-in db (vec (concat (state-db-path db-id) state-path)) new-val)))



; --- Subs ---
; ----------------------------------------------------------------------------------------------------------------------

(re-frame/reg-sub
  ::state
  (fn [db [_ db-id]]
    (get-in db (state-db-path db-id))))


(re-frame/reg-sub
  ::data
  (fn [[_ db-id data-sub]]
    [(re-frame/subscribe data-sub)
     (re-frame/subscribe [::state db-id])])

  (fn [[items state]]
    (let [sort-data (fn [coll]
                      (let [{:keys [::sort-key ::sort-comp ::sort-fn]} (::sort state)]
                        (if sort-key
                          (cond->> coll
                                   true (sort-by #(get-in (second %) sort-key) sort-fn)
                                   (= ::sort-desc sort-comp) (reverse))
                          coll)))

          paginate-data (fn [coll]
                          (let [{:keys [::cur-page ::per-page ::enabled?]} (::pagination state)]
                            (if enabled?
                              (->> coll
                                   (drop (* (or cur-page 0) (or per-page 0)))
                                   (take (or per-page 0)))
                              coll)))]

      {::visible-items (->> items
                            (map-indexed vector)
                            (sort-data)
                            (paginate-data))
       ::state         state})))


; --- Sorting ---
; ----------------------------------------------------------------------------------------------------------------------

(re-frame/reg-event-db
  ::set-sort-key
  [trim-v]
  (fn [db [db-id sort-key comp-fn]]
    (let [comp-fn (or comp-fn <)
          cur-sort-key (get-in db (sort-key-db-path db-id))
          cur-sort-comp (get-in db (sort-comp-order-db-path db-id) ::sort-asc)]
      (if (= cur-sort-key sort-key)
        (assoc-in db (sort-comp-order-db-path db-id)
                  (get {::sort-asc  ::sort-desc
                        ::sort-desc ::sort-asc} cur-sort-comp))
        (-> db
            (assoc-in (sort-key-db-path db-id) sort-key)
            (assoc-in (sort-comp-fn-db-path db-id) comp-fn)
            (assoc-in (sort-comp-order-db-path db-id) cur-sort-comp))))))



; --- Pagination ---
; ----------------------------------------------------------------------------------------------------------------------

(re-frame/reg-sub
  ::pagination-state
  (fn [[_ db-id data-sub]]
    [(re-frame/subscribe [::state db-id])
     (re-frame/subscribe data-sub)])
  (fn [[state items]]
    (let [{:keys [::pagination]} state]
      (merge
        (select-keys pagination [::per-page])
        {::cur-page (or (::cur-page pagination) 0)
         ::pages    (->> items
                         (map-indexed vector)
                         (map first)
                         (partition-all (or (::per-page pagination) 1))
                         (mapv (fn [i] [(first i) (last i)])))}))))


(re-frame/reg-event-fx
  ::select-next-page
  [trim-v]
  (fn [{:keys [db]} [db-id pagination-state]]
    (let [{:keys [::cur-page ::pages]} pagination-state]
      {:db       db
       :dispatch [::change-state-value db-id [::pagination ::cur-page] (min (inc cur-page) (dec (count pages)))]})))


(re-frame/reg-event-fx
  ::select-prev-page
  [trim-v]
  (fn [{:keys [db]} [db-id pagination-state]]
    (let [{:keys [::cur-page]} pagination-state]
      {:db       db
       :dispatch [::change-state-value db-id [::pagination ::cur-page] (max (dec cur-page) 0)]})))


(re-frame/reg-event-fx
  ::select-page
  [trim-v]
  (fn [{:keys [db]} [db-id pagination-state page-idx]]
    {:db       db
     :dispatch [::change-state-value db-id [::pagination ::cur-page] page-idx]}))


(re-frame/reg-event-fx
  ::set-per-page-value
  [trim-v]
  (fn [{:keys [db]} [db-id pagination-state per-page]]
    {:db         db
     :dispatch-n [[::select-page db-id pagination-state 0]
                  [::change-state-value db-id [::pagination ::per-page] per-page]]}))



; --- Rows Selection ---
; ----------------------------------------------------------------------------------------------------------------------

(re-frame/reg-sub
  ::selected-items
  (fn [[_ db-id data-sub]]
    [(re-frame/subscribe data-sub)
     (re-frame/subscribe [::state db-id])])

  (fn [[items state]]
    (->> items
         (map-indexed vector)
         (filter (fn [[idx _]] (contains? (get-in state [::selection ::selected-indexes]) idx)))
         (map second))))


(re-frame/reg-event-db
  ::change-row-selection
  [trim-v]
  (fn [db [db-id row-index selected?]]
    (update-in db (vec (concat (state-db-path db-id) [::selection ::selected-indexes]))
               (if selected? conj disj) row-index)))


(re-frame/reg-event-db
  ::change-table-selection
  [trim-v]
  (fn [db [db-id indexes selected?]]
    (let [selection-indexes-path (vec (concat (state-db-path db-id) [::selection ::selected-indexes]))
          selection (get-in db selection-indexes-path)]
      (assoc-in db selection-indexes-path
                (if selected?
                  (clojure.set/union (set indexes) selection)
                  (clojure.set/difference selection (set indexes)))))))


(re-frame/reg-event-db
  ::unselect-all-rows
  [trim-v]
  (fn [db [db-id]]
    (assoc-in db (vec (concat (state-db-path db-id) [::selection ::selected-indexes])) #{})))



; --- Views ---
; ----------------------------------------------------------------------------------------------------------------------

(defn datatable [db-id data-sub columns-def & [options]]
  {:pre [(or (s/valid? ::db-id db-id)
             (js/console.error (s/explain-str ::db-id db-id)))

         (or (s/valid? ::columns-def columns-def)
             (js/console.error (s/explain-str ::columns-def columns-def)))

         (or (s/valid? ::options options)
             (js/console.error (s/explain-str ::options options)))]}

  (let [view-data (re-frame/subscribe [::data db-id data-sub])]

    (reagent/create-class
      {:component-will-mount
       #(re-frame/dispatch [::on-will-mount db-id data-sub columns-def options])


       :component-did-update
       (fn [this]
         (let [[_ db-id data-sub columns-def options] (reagent/argv this)]
           (re-frame/dispatch [::on-did-update db-id data-sub columns-def options])
           (when (not= (get-in @view-data [::state ::total-items]) (count @(re-frame/subscribe data-sub)))
             (re-frame/dispatch [::select-page db-id @(re-frame/subscribe [::pagination-state db-id data-sub]) 0]))))


       :component-will-unmount
       #(re-frame/dispatch [::on-will-unmount db-id])


       :component-function
       (fn [db-id data-sub columns-def & [options]]
         (let [{:keys [::visible-items ::state]} @view-data
               {:keys [::selection]} state
               {:keys [::table-classes
                       ::tr-class-fn
                       ::header-enabled?
                       ::extra-header-row-component
                       ::footer-component
                       ::empty-tbody-component]} options]

           [:table.re-frame-datatable
            (when table-classes
              (css-class-str table-classes))

            (when-not (= header-enabled? false)
              [:thead
               (when extra-header-row-component
                 [extra-header-row-component])

               [:tr
                (when (::enabled? selection)
                  [:th {:style {:max-width "16em"}}
                   [:input {:type      "checkbox"
                            :checked   (clojure.set/subset?
                                         (->> visible-items (map first) (set))
                                         (::selected-indexes selection))
                            :on-change #(when-not (zero? (count visible-items))
                                          (re-frame/dispatch [::change-table-selection
                                                              db-id
                                                              (->> visible-items (map first) (set))
                                                              (-> % .-target .-checked)]))}]
                   [:br]
                   [:small (str (count (::selected-indexes selection)) " selected")]])

                (doall
                  (for [{:keys [::column-key ::column-label ::sorting]} columns-def]
                    ^{:key (str column-key)}
                    [:th
                     (merge
                       (when (::enabled? sorting)
                         {:style    {:cursor "pointer"}
                          :on-click #(re-frame/dispatch [::set-sort-key db-id column-key (::comp-fn sorting)])
                          :class    "sorted-by"})
                       (when (= column-key (get-in state [::sort ::sort-key]))
                         (css-class-str ["sorted-by"
                                         (condp = (get-in state [::sort ::sort-comp])
                                           ::sort-asc "asc"
                                           ::sort-desc "desc"
                                           "")])))
                     (cond
                       (string? column-label) column-label
                       (fn? column-label) [column-label]
                       :else "")]))]])


            [:tbody
             (if (empty? visible-items)
               [:tr
                [:td {:col-span (+ (count columns-def)
                                   (if (::enabled? selection) 1 0))
                      :style    {:text-align "center"}}
                 (if empty-tbody-component
                   [empty-tbody-component]
                   "no items")]]

               (doall
                 (for [[i data-entry] visible-items]
                   ^{:key i}
                   [:tr
                    (merge
                      {}
                      (when tr-class-fn
                        (css-class-str (tr-class-fn data-entry))))

                    (when (::enabled? selection)
                      [:td
                       [:input {:type      "checkbox"
                                :checked   (contains? (::selected-indexes selection) i)
                                :on-change #(re-frame/dispatch [::change-row-selection db-id i (-> % .-target .-checked)])}]])

                    (doall
                      (for [{:keys [::column-key ::render-fn ::td-class-fn]} columns-def]
                        ^{:key (str i \- column-key)}
                        [:td
                         (merge
                           {}
                           (when td-class-fn
                             (css-class-str (td-class-fn (get-in data-entry column-key) data-entry))))

                         (if render-fn
                           [render-fn (get-in data-entry column-key) data-entry]
                           (get-in data-entry column-key))]))])))]

            (when footer-component
              [:tfoot
               [footer-component]])]))})))