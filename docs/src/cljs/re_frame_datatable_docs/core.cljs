(ns re-frame-datatable-docs.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-frame-datatable-docs.events :as events]
            [re-frame-datatable-docs.subs]
            [re-frame-datatable-docs.views :as views]
            [re-frame-datatable-docs.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
