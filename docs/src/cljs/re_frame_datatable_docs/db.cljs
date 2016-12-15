(ns re-frame-datatable-docs.db
  (:require [cljs.pprint :as pp]))

(def default-db
  {:sample-data
   {:songs
    [{:index 1 :name "Black Porch Bogie" :duration 122 :play_count 2}
     {:index 2 :name "Georgia on my Mind" :duration 260 :play_count 4}
     {:index 3 :name "Salty Dog Rag" :duration 107 :play_count 1}
     {:index 4 :name "Rattenbury Rag" :duration 164 :play_count 2}
     {:index 5 :name "Steppin' Out" :duration 87 :play_count 2}
     {:index 6 :name "Black Mountain Rag" :duration 139 :play_count 3}
     {:index 7 :name "Flight of the Humminbird" :duration 191 :play_count 0}
     {:index 8 :name "Limehouse Blues" :duration 104 :play_count 1}
     {:index 9 :name "Linus and Lucy" :duration 172 :play_count 5}
     {:index 10 :name "Mostly Merle" :duration 131 :play_count 2}
     {:index 11 :name "Mozard 101" :duration 235 :play_count 11}
     {:index 12 :name "Wizard of Oz Medley" :duration 193 :play_count 4}
     {:index 13 :name "Knights of the Round Table" :duration 92 :play_count 2}]}})

