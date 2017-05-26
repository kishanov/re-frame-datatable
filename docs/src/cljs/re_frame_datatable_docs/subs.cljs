(ns re-frame-datatable-docs.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable.core :as dt]))


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
         (map #(select-keys % [:name :index :stats])))))


(re-frame/reg-sub
  ::total-play-count
  (fn []
    (re-frame/subscribe [::basic-definition-data]))
  (fn [songs-list]
    (->> songs-list
         (map #(get-in % [:stats :play_count]))
         (apply +))))



(re-frame/reg-sub
  ::pagination-data
  (fn []
    (re-frame/subscribe [::songs-list]))
  (fn [songs-list]
    (->> songs-list
         (map #(select-keys % [:name :index :stats])))))



(re-frame/reg-sub
  ::cell-rendering-data
  (fn []
    (re-frame/subscribe [::songs-list]))
  (fn [songs-list]
    (->> songs-list
         (take 5)
         (map #(select-keys % [:index :name :artist :duration :album])))))



(re-frame/reg-sub
  ::marking-elements-data
  (fn []
    (re-frame/subscribe [::songs-list]))
  (fn [songs-list]
    (->> songs-list
         (take 7)
         (map #(select-keys % [:index :name :stats])))))


(re-frame/reg-sub
  ::empty-dataset
  (fn []
    []))


(re-frame/reg-sub
  ::active-section
  (fn [db]
    (:active-section db)))

