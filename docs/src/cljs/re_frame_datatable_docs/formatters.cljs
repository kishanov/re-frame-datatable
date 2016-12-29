(ns re-frame-datatable-docs.formatters
  (:require [reagent.core :as reagent]
            [cljs.pprint :as pp]
            [clojure.walk :as walk]
            [cljs.repl :as r]
            [re-frame-datatable-docs.table-views :as table-views]))


(defn duration-formatter [seconds]
  [:span
   (let [m (quot seconds 60)
         s (mod seconds 60)]
     (if (zero? m)
       s
       (str m ":" (when (< s 10) 0) s)))])


(defn artist-formatter [artist-name]
  [:a {:href (str "https://google.com/search?q=" artist-name)}
   artist-name])


(defn album-formatter [{:keys [name year]}]
  [:span name
   [:small (str " (" year ")")]])


(defn rating-formatter [rating]
  (reagent/create-class
    {:component-function
     (fn [rating]
       [:div.ui.star.rating {:data-rating rating}])

     :component-did-mount
     (fn []
       (.ready (js/$ js/document)
               (fn []
                 (.rating (js/$ ".ui.rating") (js-obj "maxRating" 5)))))}))


(defn song-digest-formatter [name song-info]
  [:span
   {:data-tooltip (str "From album \"" (get-in song-info [:album :name]) "\"")}
   (:index song-info) ". "
   name])



(defn formatted-code [data]
  [:pre
   [:code {:class "clojure"}
    (with-out-str
      (pp/pprint
        (walk/postwalk
          (fn [x]
            (cond
              (fn? x)
              (let [fname (last (re-find #"^function re_frame_datatable_docs\$(formatters|table_views)\$(.*?)\(" (str x)))]
                (condp = fname
                  "duration_formatter"
                  (do
                    (r/source duration-formatter)
                    (println)
                    'duration-formatter)

                  "album_formatter"
                  (do
                    (r/source album-formatter)
                    (println)
                    'album-formatter)

                  "rating_formatter"
                  (do
                    (r/source rating-formatter)
                    (println)
                    'rating-formatter)

                  "song_digest_formatter"
                  (do
                    (r/source song-digest-formatter)
                    (println)
                    'song-digest-formatter)

                  "artist_formatter"
                  (do
                    (r/source artist-formatter)
                    (println)
                    'artist-formatter)

                  "aggregation_row"
                  (do
                    (r/source table-views/aggregation-row)
                    (println)
                    'aggregration-row)

                  "total_play_count_footer"
                  (do
                    (r/source table-views/total-play-count-footer)
                    (println)
                    'total-play-count-footer)

                  "play_count_td_classes"
                  (do
                    (r/source table-views/play-count-td-classes)
                    (println)
                    'play-count-td-classes)

                  "rating_td_classes"
                  (do
                    (r/source table-views/rating-td-classes)
                    (println)
                    'rating-td-classes)

                  "play_count_tr_classes"
                  (do
                    (r/source table-views/play-count-tr-classes)
                    (println)
                    'play-count-tr-classes)

                  "selected_rows_preview"
                  (do
                    (r/source table-views/selected-rows-preview))

                  (str x)))


              (and (keyword? x)
                   (re-seq #"^:re-frame-datatable.core" (str x)))
              (-> x
                  (str)
                  (clojure.string/replace #"^:re-frame-datatable.core" ":dt")
                  (keyword))

              :else
              x))
          data)))]])
