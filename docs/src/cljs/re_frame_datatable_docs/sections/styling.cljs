(ns re-frame-datatable-docs.sections.styling
  (:require [re-frame-datatable-docs.components :as components]
            [re-frame-datatable-docs.formatters :as formatters]
            [re-frame-datatable-docs.table-views :as table-views]

            [re-frame-datatable-docs.subs :as subs]
            [re-frame-datatable.core :as dt]

            [cljs.repl :as r]
            [re-frame-datatable.views :as dt-views]))


(defn css-options []
  [:div
   [:div
    "HTML table that will be generated will have a standard structure:"
    [:pre
     [:code {:class "html"}
      "<table>
  <thead>
    <tr><th>...</th><tr>
  </thead>
  <tbody>
    <tr><td>...</td></tr>
    ...
  </tbody>
</table>
"]]

    "To avoid drilling too many \"holes\" in datatable most of the styling should be done in CSS via CSS selectors based on this structure.
    To enable that, datatable allows to provide a vector of CSS classes that should be applied to "
    [:code.inline-code "<table>"] " HTML tag, and the rest can be done via CSS selectors."]
   [:div "To provide CSS classes that should be applied to <table> tag, use "
    [:code.inline-code "::table-classes"] " key in options as shown in example. Further styling can be provided via CSS selectors:"
    [:pre
     [:code {:class "css"}
      ".ui.table > thead > th { color: red; } "]]]

   [components/tabs-wrapper
    :css-options
    [::subs/basic-definition-data]
    [{::dt/column-key [:index]}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:stats :play_count]
      ::dt/column-label formatters/play-count-th}]
    {::dt/table-classes ["ui" "celled" "stripped" "table"]}
    [{:data-tab  "th-fourammter-source"
      :label     "Play Count Formatter Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source formatters/play-count-th))])}]]])




(defn styling-cells []
  [:div
   [:div
    [:p
     "Some CSS frameworks can render individual cells differently if specific classes are assigned to the " [:code.inline-code "<td>"] " element. "
     "DataTable can set these classes via column definition by providing " [:code.inline-code "::td-class-fn"] " option that accepts a function with following signature:"]
    [:pre
     [:code {:class "clojure"}
      "(defn td-class-fn [cell-value row-value]
  {:post [(seq? %)
          (every? (fn [t] (or (string? t) (nil? t))) %)]}
  ;...
)"]]

    [:p
     "i.e. function should return a sequence of CSS classes (represented as strings) that should be applied to " [:code.inline-code "<td>"] " element."]

    [components/info-message
     [:div "In the example below " [:code.inline-code "row-value"] " argument is not provided to neither of functions, because the value of class can be determined by single property only."]]]

   [components/tabs-wrapper
    :marking-cells
    [::subs/marking-elements-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:stats :play_count]
      ::dt/column-label "Play Count"
      ::dt/td-class-fn  table-views/play-count-td-classes}
     {::dt/column-key   [:stats :rating]
      ::dt/column-label "Rating"
      ::dt/td-class-fn  table-views/rating-td-classes}]
    {::dt/table-classes ["ui" "celled" "table"]}
    [{:data-tab  "css-classes-source"
      :label     "CSS Classes Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source table-views/play-count-td-classes))
                    (with-out-str (r/source table-views/rating-td-classes))])}]]])



(defn styling-rows []
  [:div
   [:div
    [:p
     "Similar capabilities are available for the complete row, with the difference that the option is defined via " [:code.inline-code "options"] " argument."
     [:code.inline-code "::tr-class-fn"] " option that accepts a function with following signature:"]
    [:pre
     [:code {:class "clojure"}
      "(defn td-class-fn [row-value]
  {:post [(seq? %)
          (every? (fn [t] (or (string? t) (nil? t))))]}
  ;...
)"]]]

   [components/tabs-wrapper
    :marking-rows
    [::subs/marking-elements-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:stats :play_count]
      ::dt/column-label "Play Count"}
     {::dt/column-key   [:stats :rating]
      ::dt/column-label "Rating"}]
    {::dt/table-classes ["ui" "celled" "table"]
     ::dt/tr-class-fn   table-views/play-count-tr-classes}
    [{:data-tab  "css-classes-source"
      :label     "CSS Classes Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source table-views/play-count-tr-classes))])}]]])
