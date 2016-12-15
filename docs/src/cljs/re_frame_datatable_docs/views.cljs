(ns re-frame-datatable-docs.views
  (:require [re-frame-datatable.core :as dt]
            [re-frame-datatable-docs.subs :as subs]
            [cljs.pprint :as pp]
            [re-frame.core :as re-frame]))


(defn sample-data []
  (let [view-data (re-frame/subscribe [::subs/sample-data])]
    (fn []
      [:small
       [:pre
        [:code
         (with-out-str (pp/pprint @view-data))]]])))



(defn main-panel []
  [:div
   [:div.ui.main.text.container
    [:h1.ui.header "re-frame-datatable"
     [:div.sub.header "DataTable component for re-frame 0.8.0+"]]

    [:div.ui.section
     [:h3.ui.dividing.header "Sample data"]
     [sample-data]]]])
