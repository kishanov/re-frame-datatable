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
     ::dt/column-label "#"}
    {::dt/column-key   [:name]
     ::dt/column-label "Name"}
    {::dt/column-key   [:duration]
     ::dt/column-label "Duration"
     ::dt/sorting      {::dt/enabled? true}
     ::dt/render-fn    (fn [val]
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

`dt/datatable` component accepts the following arguments:

* `datatable-key` - a keyword, that will be used in re-frame's `app-db` to store datatable state
* `subscription-vec` - a vector, which will be used by datatable to render data via `(re-frame/subscribe subscription-vec)`
* `columns-def` - a vector of maps, that defines how each column of datatable should look like
* `options` - optional map of additional options

For the complete documenation and live examples visit [Documentation website](https://kishanov.github.io/re-frame-datatable/).

## How it works

`re-frame-datatable` expects a re-frame subscription, that returns a collection of maps. It will render an HTML table (using `<table>` tag) based on the following rules

* Table will have `n` columns (based on the amount of elements in `columns-def` vector
* The columns will be rendered in the same order, as in `columns-def` vector
* For each item in subscription, datatable will render a row, each cell of which will correspond to the value of a particular property in the item's map (datatable uses `get-in item column-key` based on `columns-def`)
* For every action, that changes the state of datatable (sorting, pagination) it will re-calculate what should be rendered and apply it on top of provided data subscription

## License

Copyright © 2016 Kirill Ishanov

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
