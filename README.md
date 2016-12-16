# re-frame-datatable

A UI component for [re-frame](https://github.com/Day8/re-frame).
Uses existing subscription to data source in re-frame's `app-db` and declarative definition of how to render it as a table.
Supports sorting, pagination and some basic CSS manipulations for generated table.

## Usage

Leiningen

[![Leiningen version](https://clojars.org/re-frame-datatable/latest-version.svg)](http://clojars.org/re-frame-datatable)

In your application add the following dependency to the file that defines views in your re-frame application:

```clojure
(ns re-frame-datatable-docs.views
  (:require [re-frame-datatable.core :as dt]
            [your.app.subs :as subs] ; Namespace in which re-frame subscriptions are defined
           ;...))
```

Here is a sample [Reagent](https://github.com/reagent-project/reagent) component which defines datatable (assuming that `subs` namespace constains `::songs-list` subscription which returns data in the following format: `[{:index 1 :name "Mister Sandman" :duration 136} ...]`)


```clojure
(defn sneak-peek-for-readme []
  [dt/datatable
   :songs
   [::subs/songs-list]
   [{::dt/column-key   [:index]
     ::dt/sorting      {::dt/enabled? true}
     ::dt/th-classes   ["two" "wide"]
     ::dt/column-label "#"}
    {::dt/column-key   [:name]
     ::dt/th-classes   ["ten" "wide"]
     ::dt/column-label "Name"}
    {::dt/column-key   [:duration]
     ::dt/column-label "Duration"
     ::dt/sorting      {::dt/enabled? true}
     ::dt/th-classes   ["four" "wide"]
     :render-fn        (fn [val]
                         [:span
                          (let [m (quot val 60)
                                s (mod val 60)]
                            (if (zero? m)
                              s
                              (str m ":" (when (< s 10) 0) s)))])}]

   {::dt/pagination    {::dt/enabled? true
                        ::dt/per-page 5}
    ::dt/table-classes ["ui" "table" "celled"]}])
```

For the complete documenation and live examples visit [Documentation website](https://kishanov.github.io/re-frame-datatable/).


## License

Copyright © 2016 Kirill Ishanov

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
