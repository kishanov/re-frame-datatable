(ns re-frame-datatable.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [trim-v]]
            [cljs.spec :as s]))


; --- Model (spec) ---

(s/def ::db-id keyword?)
(s/def ::enabled? boolean?)
(s/def ::css-classes (s/coll-of string?))


; columns-def

(s/def ::column-key (s/coll-of keyword? :kind vector :min-count 1))
(s/def ::column-label string?)
(s/def ::sorting (s/keys :req [::enabled?]))
(s/def ::td-class-fn fn?)


(s/def ::column-def
  (s/keys :req [::column-key ::column-label]
          :opt [::sorting ::render-fn ::td-class-fn]))

(s/def ::columns-def (s/coll-of ::column-def :min-count 1))


; options

(s/def ::table-classes ::css-classes)

(s/def ::per-page (s/and integer? pos?))
(s/def ::cur-page (s/and integer? (complement neg?)))
(s/def ::total-pages (s/and integer? pos?))
(s/def ::pagination
  (s/keys :req [::enabled?]
          :opt [::per-page ::cur-page ::total-pages]))

(s/def ::selection
  (s/keys :req [::enabled?]))

(s/def ::extra-header-row-component fn?)
(s/def ::footer-component fn?)

(s/def ::options
  (s/nilable
    (s/keys :opt [::pagination ::table-classes ::selection ::extra-header-row-component ::footer-component])))


; --- Re-frame database paths ---

(def root-db-path [::re-frame-datatable])
(defn db-path-for [db-path db-id]
  (vec (concat (conj root-db-path db-id)
               db-path)))

(def columns-def-db-path (partial db-path-for [::columns-def]))
(def options-db-path (partial db-path-for [::options]))
(def state-db-path (partial db-path-for [::state]))
(def sort-key-db-path (partial db-path-for [::state ::sort ::sort-key]))
(def sort-comp-db-path (partial db-path-for [::state ::sort ::sort-comp]))


; --- Defaults ---

(def per-page 10)


; --- Utils ---
(defn css-class-str [classes]
  {:class (->> classes
               (filter (complement nil?))
               (clojure.string/join \space))})



; --- Events ---

(re-frame/reg-event-db
  ::mount
  [trim-v]
  (fn [db [db-id columns-def options]]
    (-> db
        (assoc-in (columns-def-db-path db-id)
                  columns-def)
        (assoc-in (options-db-path db-id)
                  options)
        (assoc-in (state-db-path db-id)
                  {::pagination (merge {::per-page per-page
                                        ::cur-page 0}
                                       (select-keys (::pagination options) [::per-page ::enabled?]))
                   ::selection  (merge {::selected-indexes #{}}
                                       (select-keys (::selection options) [::enabled?]))}))))


(re-frame/reg-event-db
  ::unmount
  [trim-v]
  (fn [db [db-id]]
    (update-in db root-db-path dissoc db-id)))


(re-frame/reg-event-db
  ::set-sort-key
  [trim-v]
  (fn [db [db-id sort-key]]
    (let [cur-sort-key (get-in db (sort-key-db-path db-id))
          cur-sort-comp (get-in db (sort-comp-db-path db-id) >)]
      (if (= cur-sort-key sort-key)
        (assoc-in db (sort-comp-db-path db-id)
                  (get {> < < >} cur-sort-comp))
        (-> db
            (assoc-in (sort-key-db-path db-id) sort-key)
            (assoc-in (sort-comp-db-path db-id) cur-sort-comp))))))


(re-frame/reg-event-db
  ::change-state-value
  [trim-v]
  (fn [db [db-id state-path new-val]]
    (assoc-in db (vec (concat (state-db-path db-id) state-path)) new-val)))


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
    (assoc-in db (vec (concat (state-db-path db-id) [::selection ::selected-indexes]))
              (if selected? (set indexes) #{}))))


(re-frame/reg-event-db
  ::unselect-all-rows
  [trim-v]
  (fn [db [db-id]]
    (assoc-in db (vec (concat (state-db-path db-id) [::selection ::selected-indexes])) #{})))


; --- Subs ---

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
                      (let [{:keys [::sort-key ::sort-comp]} (::sort state)]
                        (if sort-key
                          (sort-by #(get-in (second %) sort-key) sort-comp coll)
                          coll)))

          paginate-data (fn [coll]
                          (let [{:keys [::cur-page ::per-page ::enabled?]} (::pagination state)]
                            (if enabled?
                              (->> coll
                                   (drop (* cur-page per-page))
                                   (take per-page))
                              coll)))]

      {::items   (->> items
                      (map-indexed vector)
                      (sort-data)
                      (paginate-data))
       ::indexes (set (range (count items)))
       ::state   (-> state
                     (assoc-in [::pagination ::total-pages]
                               (Math/ceil (/ (count items) (get-in state [::pagination ::per-page])))))})))


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



; --- Views ---

(defn page-selector [db-id pagination]
  (let [{:keys [::total-pages ::cur-page]} pagination]
    [:div.page-selector
     {:style {:float         "right"
              :margin-bottom "1em"}}
     (let [prev-enabled? (not= cur-page 0)]
       [:span
        (merge
          {:on-click #(when prev-enabled?
                        (re-frame/dispatch [::change-state-value
                                            db-id
                                            [::pagination ::cur-page]
                                            (dec cur-page)]))
           :style    {:cursor "pointer"}}
          (when-not prev-enabled?
            {:disabled "disabled"}))
        (str \u25C4 " PREVIOUS ")])


     [:select
      {:value     cur-page
       :on-change #(re-frame/dispatch [::change-state-value
                                       db-id
                                       [::pagination ::cur-page]
                                       (js/parseInt (-> % .-target .-value))])}
      (doall
        (for [page-index (range total-pages)]
          ^{:key page-index}
          [:option
           {:value page-index}
           (str "Page " (inc page-index) " of " total-pages)]))]

     (let [next-enabled? (not= cur-page (dec total-pages))]
       [:span
        (merge
          {:on-click #(when next-enabled?
                        (re-frame/dispatch [::change-state-value
                                            db-id
                                            [::pagination ::cur-page]
                                            (inc cur-page)]))
           :style    {:cursor "pointer"}}
          (when-not next-enabled?
            {:disabled "disabled"}))
        (str " NEXT " \u25BA)])]))


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
       #(re-frame/dispatch [::mount db-id columns-def options])


       :component-will-unmount
       #(re-frame/dispatch [::unmount db-id])


       :component-function
       (fn [db-id data-sub columns-def & [options]]
         (let [{:keys [::items ::state ::indexes]} @view-data
               {:keys [::selection ::pagination]} state
               {:keys [::table-classes ::tr-class-fn ::extra-header-row-component ::footer-component]} options]
           [:div.re-colls-datatable
            (when (::enabled? pagination)
              [page-selector db-id pagination])

            [:table
             (when table-classes
               (css-class-str table-classes))

             [:thead
              (when extra-header-row-component
                [extra-header-row-component])

              [:tr
               (when (::enabled? selection)
                 [:th
                  [:input {:type      "checkbox"
                           :checked   (= (::selected-indexes selection) indexes)
                           :on-change #(re-frame/dispatch [::change-table-selection db-id indexes (-> % .-target .-checked)])}]])

               (doall
                 (for [{:keys [::column-key ::column-label ::sorting]} columns-def]
                   ^{:key (str column-key)}
                   [:th
                    (merge
                      (when (::enabled? sorting)
                        {:style    {:cursor "pointer"}
                         :on-click #(re-frame/dispatch [::set-sort-key db-id column-key])})
                      (when (= column-key (get-in state [::sort ::sort-key]))
                        (css-class-str ["sorted-by"
                                        (if (= < (get-in state [::sort ::sort-comp]))
                                          "asc"
                                          "desc")])))
                    column-label]))]]

             [:tbody
              (doall
                (for [[i data-entry] items]
                  ^{:key i}
                  [:tr
                   (merge
                     {}
                     (when tr-class-fn
                       (css-class-str (tr-class-fn data-entry))))

                   (let [{:keys [::selection]} state]
                     (when (::enabled? selection)
                       [:td
                        [:input {:type      "checkbox"
                                 :checked   (contains? (::selected-indexes selection) i)
                                 :on-change #(re-frame/dispatch [::change-row-selection db-id i (-> % .-target .-checked)])}]]))


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
                          (get-in data-entry column-key))]))]))]

             (when footer-component
               [:tfoot
                [footer-component]])]]))})))
