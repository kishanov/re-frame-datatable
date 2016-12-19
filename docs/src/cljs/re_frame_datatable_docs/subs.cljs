(ns re-frame-datatable-docs.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [cljs.pprint :as pp]))


(re-frame/reg-sub
  ::songs-list
  (fn [db]
    (get-in db [:sample-data :songs])))


(re-frame/reg-sub
  ::basic-definition-data
  (fn []
    (re-frame/subscribe [::songs-list]))
  (fn [songs-list]
    (->> songs-list
         (take 5)
         (map #(select-keys % [:name :index :play_count])))))



(re-frame/reg-sub
  ::pagination-data
  (fn []
    (re-frame/subscribe [::songs-list]))
  (fn [songs-list]
    (->> songs-list
         (map #(select-keys % [:name :index :play_count])))))



(re-frame/reg-sub
  ::cell-rendering-data
  (fn []
    (re-frame/subscribe [::songs-list]))
  (fn [songs-list]
    (->> songs-list
         (take 5)
         (map #(select-keys % [:index :name :artist :duration :album :rating])))))
