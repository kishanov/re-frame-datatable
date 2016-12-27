(ns re-frame-datatable-docs.table-views
  (:require [re-frame.core :as re-frame]
            [re-frame-datatable-docs.subs]))



(defn aggregation-row []
  [:tr
   [:th {:col-span 1} ""]
   [:th {:col-span 3} "Album info"]])



(defn total-play-count-footer []
  ; ::total-play-count subscription returns sum of all :play_count values
  ; for ::basic-definition-data subscription
  (let [total-count (re-frame/subscribe [::re-frame-datatable-docs.subs/total-play-count])]
    [:tr
     [:th {:col-span 2}]
     [:th
      [:strong (str @total-count " total")]]]))



(defn play-count-td-classes [play-count]
  [(when (zero? play-count)
     "disabled")])



(defn rating-td-classes [rating]
  [(cond
     (= 5 rating) "positive"
     (#{3 4} rating) "warning"
     (nil? rating) nil                                      ; DataTable will ignore all nil values in classes vector
     :else "negative")])


(defn play-count-tr-classes [data-entry]
  [(when-not (pos? (:play_count data-entry))
     "warning")])
