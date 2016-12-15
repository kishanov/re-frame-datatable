(ns re-frame-datatable-docs.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [cljs.pprint :as pp]))


(re-frame/reg-sub
  ::songs-list
  (fn [db]
    (get-in db [:sample-data :songs])))



(re-frame/reg-sub
  ::sample-data
  (fn [db]
    (:sample-data db)))
