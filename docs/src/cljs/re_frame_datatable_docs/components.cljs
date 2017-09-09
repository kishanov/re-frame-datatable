(ns re-frame-datatable-docs.components
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable-docs.formatters :as formatters]
            [re-frame-datatable.core :as dt]))



(defn icon-message [message-class icon-class text & [header]]
  [:div.ui.icon.message
   {:class message-class}
   [:i.icon {:class icon-class}]
   [:div.content
    (when header
      [:div.header header])
    text]])


(def warning-message (partial icon-message "warning" "warning sign"))
(def info-message (partial icon-message "info" "info circle"))



(defn tabs-wrapper [dt-id data-sub columns-def options & [extra-tabs dt-container]]
  (let [data (re-frame/subscribe data-sub)
        example-dom-id (str (name dt-id) "-example")
        usage-dom-id (str (name dt-id) "-usage")
        data-dom-id (str (name dt-id) "-data")
        extra-tabs (map #(assoc % :data-tab (str (name dt-id) (:data-tab %)))
                        extra-tabs)]

    (fn []
      (let [dt-def [dt/datatable dt-id data-sub columns-def options]]
        [:div {:style {:margin-top "2em"}}
         [:div.ui.top.attached.tabular.menu
          [:a.active.item
           {:data-tab example-dom-id} "Example"]
          [:a.item
           {:data-tab usage-dom-id} "Usage"]
          [:a.item
           {:data-tab data-dom-id} "Data"]
          (doall
            (for [{:keys [data-tab label]} extra-tabs]
              ^{:key data-tab}
              [:a.item
               {:data-tab data-tab} label]))]

         [:div.ui.bottom.attached.active.tab.segment
          {:data-tab example-dom-id}
          (if dt-container
            [dt-container dt-def]
            dt-def)]

         [:div.ui.bottom.attached.tab.segment
          {:data-tab usage-dom-id}
          [formatters/formatted-code
           (vec (cons `dt/datatable
                      (->> dt-def (rest) (filter (complement nil?)))))]]

         [:div.ui.bottom.attached.tab.segment
          {:data-tab data-dom-id}
          [formatters/formatted-code @data]]

         (doall
           (for [{:keys [data-tab component]} extra-tabs]
             ^{:key data-tab}
             [:div.ui.bottom.attached.tab.segment
              {:data-tab data-tab}
              [component]]))]))))
