(ns re-frame-datatable-docs.sections.additional-structure
  (:require [re-frame-datatable-docs.components :as components]
            [re-frame-datatable-docs.formatters :as formatters]
            [re-frame-datatable-docs.table-views :as table-views]
            [cljs.repl :as r]

            [re-frame-datatable-docs.subs :as subs]
            [re-frame-datatable.core :as dt]
            [re-frame-datatable.views :as dt-views]

            [re-frame.core :as re-frame]))



(defn disable-header []
  [:div
   [:p
    "It is also possible to configure DataTable not to render header at all - "
    "just pass " [:code.inline-code "::header-enabled?"] " option set to false."]

   [components/warning-message
    [:div
     "Notice that " [:code.inline-code "::header-enabled?"] " has precedence over any of the specified " [:code.inline-code "::column-label"]
     "In the example below, header is not rendered even after " [:code.inline-code "::column-label"] " was specified for the second column."]]

   [components/tabs-wrapper
    :basic-definition-no-header
    [::subs/basic-definition-data]
    [{::dt/column-key [:index]}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}]
    {::dt/header-enabled? false}]])


(defn empty-dataset []
  [:div
   [:div
    [:p
     "In case if given subscription returns an empty datasert, DataTable will render single row with all " [:code.inline-code "<td>"]
     " elements collapsed into single one via " [:code.inline-code ":col-span"] " attribute."]
    [:p
     "If you want to provide a custom Reagent component to be rendered inside this empty row, use "
     [:code.inline-code "::empty-tbody-component"] " option."]]

   [components/tabs-wrapper
    :empty-tbody
    [::subs/empty-dataset]
    [{::dt/column-key   [:index]
      ::dt/column-label "Index"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:stats :play_count]
      ::dt/column-label "Play count"}]
    {::dt/table-classes         ["ui" "celled" "table"]
     ::dt/empty-tbody-component table-views/empty-tbody-formatter
     ::dt/pagination            {::dt/enabled? true}}
    [{:data-tab  "empty-tbody-source"
      :label     "Empty Row Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source table-views/empty-tbody-formatter))])}]

    (fn [dt-def]
      [:div.ui.grid
       [:div.row
        [:div.right.aligned.column
         [dt-views/default-pagination-controls :empty-tbody [::subs/empty-dataset]]]]
       [:div.row
        [:div.column
         dt-def]]])]])



(defn extra-header []
  [:div
   [:div
    "DataTable supports rendering of an additional row inside " [:code.inline-code "<thead>"]
    " (it will be rendered on top of header row, rendered from " [:code.inline-code "columns-def"] " vector). "
    "To render it, use " [:code.inline-code "::extra-header-row-component"] " option key with the valid Reagent component passed as a value."

    [components/warning-message
     [:div
      " Notice that DataTable will not validate that passed component is in fact valid, it just checks if it's a function."
      "Internally, it will be rendered as " [:code.inline-code "[(::extra-header-row-component options)]"]]]]

   [components/tabs-wrapper
    :extra-header-row
    [::subs/cell-rendering-data]
    [{::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:artist]
      ::dt/column-label "Artist"}
     {::dt/column-key   [:album :name]
      ::dt/column-label "Album"}
     {::dt/column-key   [:album :year]
      ::dt/column-label "Year"}]
    {::dt/table-classes              ["ui" "celled" "table"]
     ::dt/extra-header-row-component table-views/aggregation-row}
    [{:data-tab  "aggregation-row-source"
      :label     "Aggregation Row Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source table-views/aggregation-row))])}]]])


(defn footer []
  [:div
   [:div
    "DataTable can also render an arbitrary Reagent comopnent inside " [:code.inline-code "<tfoot>"] " HTML tag."
    "To render it, use " [:code.inline-code "::footer-component"] " option key with the valid Reagent component passed as a value."

    [components/info-message
     [:div
      "Notice that in the example below " [:code.inline-code "total-play-count-footer"] " uses additional subscription to get total play count. "
      "DataTable doesn't maintain any intermediate state of given data, so in order to access it - use the same subscription as passed to DataTable (or the one which is build on top of it)"]]]

   [components/tabs-wrapper
    :footer-component
    [::subs/basic-definition-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "Index"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:stats :play_count]
      ::dt/column-label "Play count"}]
    {::dt/table-classes    ["ui" "celled" "table"]
     ::dt/footer-component table-views/total-play-count-footer}
    [{:data-tab  "footer-source"
      :label     "Footer Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source table-views/total-play-count-footer))])}]]])
