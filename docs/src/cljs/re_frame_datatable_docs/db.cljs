(ns re-frame-datatable-docs.db)

(def default-db
  {:sample-data    {:songs
                    (->>
                      [{:index 1 :name "Black Porch Bogie" :duration 122 :stats {:play_count 2 :rating 5}}
                       {:index 2 :name "Georgia on my Mind" ::duration 260 :stats {:play_count 0}}
                       {:index 3 :name "Salty Dog Rag" :duration 107 :stats {:play_count 1 :rating 2}}
                       {:index 4 :name "Rattenbury Rag" :duration 164 :stats {:play_count 2 :rating 3}}
                       {:index 5 :name "Steppin' Out" :duration 87 :stats {:play_count 0}}
                       {:index 6 :name "Black Mountain Rag" :duration 139 :stats {:play_count 3 :rating 4}}
                       {:index 7 :name "Flight of the Humminbird" :duration 191 :stats {:play_count 0}}
                       {:index 8 :name "Limehouse Blues" :duration 104 :stats {:play_count 1}}
                       {:index 9 :name "Linus and Lucy" :duration 172 :stats {:play_count 5 :rating 5}}
                       {:index 10 :name "Mostly Merle" :duration 131 :stats {:play_count 2}}
                       {:index 11 :name "Mozard 101" :duration 235 :stats {:play_count 11 :rating 4}}
                       {:index 12 :name "Wizard of Oz Medley" :duration 193 :stats {:play_count 4}}
                       {:index 13 :name "Knights of the Round Table" :duration 92 :stats {:play_count 2}}]
                      (map #(-> %
                                (assoc :artist "Buster B. Jones")
                                (assoc :album {:name "A Decade of Buster B. Jones"
                                               :year 2005}))))}
   :active-section (let [anchor (clojure.string/replace js/window.location.hash #"^\#" "")]
                     (if (#{"usage"
                            "basic"
                            "css-options"
                            "pagination"
                            "sorting"
                            "cell-rendering"
                            "rows-selection"
                            "additional-structure"
                            "marking-individual-elements"}
                           anchor)
                       anchor
                       "usage"))})

