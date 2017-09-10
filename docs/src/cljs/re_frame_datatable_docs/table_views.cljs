(ns re-frame-datatable-docs.table-views
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable.core]
            [re-frame-datatable-docs.subs]))



(defn aggregation-row []
  [:tr
   [:th {:col-span 1} ""]
   [:th {:col-span 3} "Album info"]])



(defn total-play-count-footer []
  ; ::total-play-count subscription returns sum of all :play_count values
  ; for ::basic-definition-data subscription
  (let [total-count (re-frame/subscribe [::re-frame-datatable-docs.subs/total-play-count])]
    [:tr
     [:th {:col-span 2}]
     [:th
      [:strong (str @total-count " total")]]]))



(defn play-count-td-classes [play-count]
  "Set 'disabled' class for the song if it hasn't been played yet"
  [(when (zero? play-count)
     "disabled")])



(defn rating-td-classes [rating]
  "Color-code cell by rating"
  [(cond
     (= 5 rating) "positive"
     (#{3 4} rating) "warning"
     (nil? rating) nil                                      ; DataTable will ignore all nil values in classes vector
     :else "negative")])



(defn play-count-tr-classes
  "For all songs that hasn't been played yet, define <tr> class as 'warning'"
  [song]
  [(when-not (pos? (get-in song [:stats :play_count]))
     "warning")])


(defn selected-rows-preview []
  [:pre
   [:code
    @(re-frame/subscribe [::re-frame-datatable.core/selected-items
                          :rows-basic-definition
                          [::re-frame-datatable-docs.subs/basic-definition-data]])]])


(defn empty-tbody-formatter []
  [:em
   "DataTable receieved an empty dataset via subscription"])



(defn basic-pagination [db-id data-sub]
  (let [pagination-state (re-frame/subscribe [::re-frame-datatable.core/pagination-state db-id data-sub])]
    (fn []
      (let [{:keys [::re-frame-datatable.core/cur-page ::re-frame-datatable.core/pages]} @pagination-state
            total-pages (count pages)
            next-enabled? (< cur-page (dec total-pages))
            prev-enabled? (pos? cur-page)]

        [:div.ui.pagination.menu
         [:a.item
          {:on-click #(when prev-enabled?
                        (re-frame/dispatch [::re-frame-datatable.core/select-prev-page db-id @pagination-state]))
           :class    (when-not prev-enabled? "disabled")}
          [:i.left.chevron.icon]]

         (for [i (range total-pages)]
           ^{:key i}
           [:a.item
            {:class    (when (= i cur-page) "active")
             :on-click #(re-frame/dispatch [::re-frame-datatable.core/select-page db-id @pagination-state i])}
            (inc i)])

         [:a.item
          {:on-click #(when next-enabled?
                        (re-frame/dispatch [::re-frame-datatable.core/select-next-page db-id @pagination-state]))
           :class    (when-not next-enabled? "disabled")}
          [:i.right.chevron.icon]]]))))



(defn gmail-like-pagination [db-id data-sub]
  (let [pagination-state (re-frame/subscribe [::re-frame-datatable.core/pagination-state db-id data-sub])]
    (fn []
      (let [{:keys [::re-frame-datatable.core/cur-page ::re-frame-datatable.core/pages]} @pagination-state
            total-pages (count pages)
            next-enabled? (< cur-page (dec total-pages))
            prev-enabled? (pos? cur-page)]

        [:div
         [:div {:style {:display      "inline-block"
                        :margin-right ".5em"}}
          [:strong
           (str (inc (first (get pages cur-page))) "-" (inc (second (get pages cur-page))))]
          [:span " of "]
          [:strong (inc (second (last pages)))]]

         [:div.ui.pagination.mini.menu
          [:a.item
           {:on-click #(when prev-enabled?
                         (re-frame/dispatch [::re-frame-datatable.core/select-prev-page db-id @pagination-state]))
            :class    (when-not prev-enabled? "disabled")}
           [:i.left.chevron.icon]]

          [:a.item
           {:on-click #(when next-enabled?
                         (re-frame/dispatch [::re-frame-datatable.core/select-next-page db-id @pagination-state]))
            :class    (when-not next-enabled? "disabled")}
           [:i.right.chevron.icon]]]]))))



(defn per-page-radio-selector [dt-id data-sub]
  (let [pagination-state (re-frame/subscribe [::re-frame-datatable.core/pagination-state dt-id data-sub])
        per-page-values [5 10 15]]

    (fn []
      (let [{:keys [::re-frame-datatable.core/per-page]} @pagination-state]
        [:div.ui.form
         [:div.inilne.fields
          (doall
            (for [per-page-option per-page-values]
              ^{:key per-page-option}
              [:div.field
               [:input
                {:type      :radio
                 :on-change #(re-frame/dispatch [::re-frame-datatable.core/set-per-page-value
                                                 dt-id data-sub
                                                 (js/parseInt (-> % .-target .-name))])
                 :name      per-page-option
                 :class     "hidden"
                 :checked   (= per-page per-page-option)}]
               [:label per-page-option]]))]]))))

