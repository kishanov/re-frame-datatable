(ns re-frame-datatable-docs.sections.sorting
  (:require [re-frame-datatable-docs.components :as components]
            [re-frame-datatable-docs.formatters :as formatters]
            [re-frame-datatable-docs.table-views :as table-views]
            [re-frame-datatable-docs.subs :as subs]
            [re-frame-datatable.core :as dt]
            [cljs.repl :as r]
            [re-frame-datatable.views :as dt-views]))



(defn enable-sorting []
  [:div
   [:div
    "Sorting is enabled on per-column basis. To make column sortable just add "
    [:code.inline-code "::sorting"] " with the value "
    [:code.inline-code "{::enabled? true}"] " to particular column definition in "
    [:code.inline-code "columns-def"] " vector. "
    "In the example below, index and play_count columns are made sortable."]

   [components/info-message
    [:div " To sort table, click on a column header (for the column on which sorting was enabled)"]]

   [components/warning-message
    [:div
     " When the table was sorted by particular column, the <th> element of sorted column will have 2 HTML classes assigned: "
     [:code.inline-code "sorted-by"] " and either " [:code.inline-code "asc"] " or " [:code.inline-code "desc"]
     ". This allows to apply CSS styling to this column to emphasize that the table was sorted by it. DataTable doesn't render additional visual clues to show emphasize it."]]


   [components/tabs-wrapper
    :sorting
    [::subs/basic-definition-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"
      ::dt/sorting      {::dt/enabled? true}}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:stats :play_count]
      ::dt/column-label "Play count"
      ::dt/sorting      {::dt/enabled? true}}]
    {::dt/table-classes ["ui" "table"]}
    [{:data-tab  "css-example"
      :label     "CSS"
      :component (fn []
                   [:pre
                    [:code {:class "css"}
                     "
 table.re-frame-datatable > thead th.sorted-by:after {
     display: inline-block;
 }

 table.re-frame-datatable > thead th.sorted-by.desc:after {
     content: '\f0d7';
 }

 table.re-frame-datatable > thead th.asc:after {
     content: '\f0d8';
 }

 table.re-frame-datatable > thead th:after {
     display: none;
     font-family: Icons;
     margin-left: .5em;
 }"]])}]]])



(defn custom-sorting-fn []
  [:div
   [:div
    "To define custom comparator for sorting function, provide "
    [:code.inline-code "::comp-fn"] " with the function that accepts a function that will be used internally as a third argument to "
    [:a {:href "https://clojuredocs.org/clojure.core/sort-by"} "sort-by"] " (DataTable uses it internally in to sort table data). "
    "Note that DataTable requires a function, so things like keywords should be wrapped into function with proper signature."]

   [components/tabs-wrapper
    :sorting
    [::subs/basic-definition-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:stats]
      ::dt/column-label "Play count"
      ::dt/sorting      {::dt/enabled? true
                         ::dt/comp-fn  formatters/sort-play-count-comp}}]
    {::dt/table-classes ["ui" "table"]}
    [{:data-tab  "comparator-fn-source"
      :label     "Comparator Fn Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source formatters/sort-play-count-comp))])}]]])
