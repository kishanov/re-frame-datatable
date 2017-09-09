(ns re-frame-datatable.views
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable.core :as dt]))



(defn default-pagination-controls [db-id data-sub]
  (let [pagination-state
        (re-frame/subscribe
          [::dt/pagination-state db-id data-sub])]
    (fn []
      (let [{:keys [::dt/cur-page
                    ::dt/pages]} @pagination-state
            total-pages (if (pos? (count pages)) (count pages) 1)]
        [:div.re-frame-datatable.page-selector
         (let [prev-enabled? (pos? cur-page)]
           [:span
            {:on-click
                    #(when prev-enabled?
                       (re-frame/dispatch
                         [::dt/select-prev-page
                          db-id @pagination-state]))
             :style {:cursor (when prev-enabled? "pointer")
                     :color  (when-not prev-enabled? "rgba(40,40,40,.3)")}}
            (str \u25C4 " PREVIOUS ")])

         [:select
          {:value     cur-page
           :on-change #(re-frame/dispatch
                         [::dt/select-page
                          db-id @pagination-state
                          (js/parseInt (-> % .-target .-value))])}
          (doall
            (for [page-index (range total-pages)]
              ^{:key page-index}
              [:option
               {:value page-index}
               (str "Page " (inc page-index) " of " total-pages)]))]

         (let [next-enabled? (< cur-page (dec total-pages))]
           [:span
            {:style    {:cursor (when next-enabled? "pointer")
                        :color  (when-not next-enabled? "rgba(40,40,40,.3)")}
             :on-click #(when next-enabled?
                          (re-frame/dispatch
                            [::dt/select-next-page
                             db-id @pagination-state]))}
            (str " NEXT " \u25BA)])]))))
