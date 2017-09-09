(ns re-frame-datatable-docs.formatters
  (:require [reagent.core :as reagent]
            [cljs.pprint :as pp]
            [clojure.walk :as walk]))


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
                (symbol (clojure.string/replace fname #"_" "-")))

              (and (keyword? x)
                   (re-seq #"^:re-frame-datatable.core" (str x)))
              (-> x
                  (str)
                  (clojure.string/replace #"^:re-frame-datatable.core" ":dt")
                  (keyword))

              :else
              x))
          data)))]])



(defn formatted-function-def [& sources]
  [:div
   (for [[i source] (map-indexed vector sources)]
     ^{:key i}
     [:pre
      [:code {:class "clojure"}
       source]])
   [:br]])



(defn play-count-th []
  [:div
   {:data-tooltip "Play count"}
   [:i.music.icon]])



(defn sort-play-count-comp [x y]
  (compare (:play_count x) (:play_count y)))
