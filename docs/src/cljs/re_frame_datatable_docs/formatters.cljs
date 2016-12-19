(ns re-frame-datatable-docs.formatters)


(defn duration-formatter [seconds]
  [:span
   (let [m (quot seconds 60)
         s (mod seconds 60)]
     (if (zero? m)
       s
       (str m ":" (when (< s 10) 0) s)))])



