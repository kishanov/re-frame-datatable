(ns re-frame-datatable-docs.formatters
  (:require [reagent.core :as reagent]))


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


(defn aggregation-row []
  [:tr
   [:th {:col-span 1} ""]
   [:th {:col-span 3} "Album info"]])
