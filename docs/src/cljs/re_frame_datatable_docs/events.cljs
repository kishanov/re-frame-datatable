(ns re-frame-datatable-docs.events
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable-docs.db :as db]))

(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))
