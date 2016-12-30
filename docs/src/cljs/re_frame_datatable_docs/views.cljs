(ns re-frame-datatable-docs.views
  (:require [re-frame-datatable.core :as dt]
            [re-frame-datatable-docs.subs :as subs]
            [re-frame.core :as re-frame]
            [re-frame-datatable-docs.formatters :as formatters]
            [re-frame-datatable-docs.table-views :as table-views]
            [reagent.core :as reagent]
            [cljs.repl :as r]))


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



(defn tabs-wrapper [dt-id data-sub columns-def options & [extra-tabs dt-container]]
  (let [data (re-frame/subscribe data-sub)
        example-dom-id (str (name dt-id) "-example")
        usage-dom-id (str (name dt-id) "-usage")
        data-dom-id (str (name dt-id) "-data")
        extra-tabs (map #(assoc % :data-tab (str (name dt-id) (:data-tab %)))
                        extra-tabs)]

    (fn []
      (let [dt-def [dt/datatable dt-id data-sub columns-def options]]
        [:div {:style {:margin-top "2em"}}
         [:div.ui.top.attached.tabular.menu
          [:a.active.item
           {:data-tab example-dom-id} "Example"]
          [:a.item
           {:data-tab usage-dom-id} "Usage"]
          [:a.item
           {:data-tab data-dom-id} "Data"]
          (doall
            (for [{:keys [data-tab label]} extra-tabs]
              ^{:key data-tab}
              [:a.item
               {:data-tab data-tab} label]))]

         [:div.ui.bottom.attached.active.tab.segment
          {:data-tab example-dom-id}
          (if dt-container
            [dt-container dt-def]
            dt-def)]

         [:div.ui.bottom.attached.tab.segment
          {:data-tab usage-dom-id}
          [formatters/formatted-code
           (vec (cons `dt/datatable
                      (->> dt-def (rest) (filter (complement nil?)))))]]

         [:div.ui.bottom.attached.tab.segment
          {:data-tab data-dom-id}
          [formatters/formatted-code @data]]

         (doall
           (for [{:keys [data-tab component]} extra-tabs]
             ^{:key data-tab}
             [:div.ui.bottom.attached.tab.segment
              {:data-tab data-tab}
              [component]]))]))))



(defn icon-message [message-class icon-class text & [header]]
  [:div.ui.icon.message
   {:class message-class}
   [:i.icon {:class icon-class}]
   [:div.content
    (when header
      [:div.header header])
    text]])


(def warning-message (partial icon-message "warning" "warning sign"))
(def info-message (partial icon-message "info" "info circle"))



(defn usage-section []
  [:div
   [:div "re-frame-datatable should be used as any other Reagent component. First, require it in the file that contains your re-frame application views:"]
   [:pre
    [:code {:class "clojure"}
     "(:require [re-frame-datatable.core :as dt])"]]

   [:p "Then define a Reagent component which uses a datatable:"]

   [:pre
    [:code {:class "clojure"}
     "(defn my-component []
  [dt/datatable
   datatable-key ; a keyword, that will be used in re-frame's `app-db` to store datatable state
   subscription-vec ; a vector, which will be used by datatable to render data via `(re-frame/subscribe subscription-vec)`
   columns-def ; a vector of maps, that defines how each column of datatable should look like
   options] ; optional map of additional options)"]]])


(defn main-header []
  [:div.ui.grid
   [:div.twelve.wide.column
    [:h1.ui.header
     {:style {:margin-bottom "2em"
              :margin-top    "1em"}}
     "re-frame-datatable"
     [:div.sub.header "DataTable component for re-frame 0.8.0+"]]]
   [:div.four.wide.column
    [:div
     [:div.ui.right.floated.menu
      {:style {:margin-top "2em"}}
      [:a.item
       {:href          "https://github.com/kishanov/re-frame-datatable"
        :data-tooltip  "View project on GitHub"
        :data-position "bottom center"}
       [:i.github.icon]]]]]])


(defn basic-definition []
  [:div
   [:div
    "There are only 2 mandatory definitions that should be provided for each map in "
    [:code.inline-code "columns-def"] " vector:"
    [:ul.ui.list
     [:li [:code.inline-code "::column-key"] " - a vector that is used to access value of each item via " [:code "get-in"]]
     [:li [:code.inline-code "::column-label"] " - a string that will be a header for a column"]]]
   [tabs-wrapper
    :basic-definition
    [::subs/basic-definition-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:play_count]
      ::dt/column-label "Play count"}]]])


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

   [tabs-wrapper
    :css-options
    [::subs/basic-definition-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:play_count]
      ::dt/column-label "Play count"}]
    {::dt/table-classes ["ui" "celled" "stripped" "table"]}]])



(defn pagination []
  [:div
   [:div
    "Pagination can be enabled via " [:code.inline-code "::pagination"] " key. There are 2 options:"
    [:ul.ui.list
     [:li [:code.inline-code "enabled?"] " - boolean to define if pagination should be enabled"]
     [:li [:code.inline-code "per-page"] " - integer to define how many elements should be shown per page (default is 10)"]]]

   [tabs-wrapper
    :pagination
    [::subs/pagination-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:play_count]
      ::dt/column-label "Play count"}]
    {::dt/pagination    {::dt/enabled? true
                         ::dt/per-page 5}
     ::dt/table-classes ["ui" "table"]}
    [{:data-tab  "default-pagination-controls-usage"
      :label     "Default Pagination Controls Usage"
      :component (fn []
                   [:pre
                    [:code {:class "clojure"}
                     "[dt/default-pagination-controls :pagination [::subs/pagination-data]]"]])}]
    (fn [dt-def]
      [:div.ui.grid
       [:div.row
        [:div.right.aligned.column
         [dt/default-pagination-controls :pagination [::subs/pagination-data]]]]
       [:div.row
        [:div.column
         dt-def]]
       [:div.row
        [:div.right.aligned.column
         [dt/default-pagination-controls :pagination [::subs/pagination-data]]]]])]


   [tabs-wrapper
    :pagination-custom-controls
    [::subs/pagination-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:play_count]
      ::dt/column-label "Play count"}]
    {::dt/pagination    {::dt/enabled? true
                         ::dt/per-page 5}
     ::dt/table-classes ["ui" "table"]}
    [{:data-tab  "pagination-controls-source"
      :label     "Pagination Controls Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source table-views/basic-pagination))
                    (with-out-str (r/source table-views/gmail-like-pagination))])}]
    (fn [dt-def]
      [:div.ui.two.column.grid
       [:div.column dt-def]
       [:div.column
        [:div {:style {:margin-bottom "1em"}}
         [:h5.ui.herader "Basic pagination controls"]
         [table-views/basic-pagination :pagination-custom-controls [::subs/pagination-data]]]

        [:div {:style {:margin-bottom "1em"}}
         [:h5.ui.herader "GMAIL-like pagination controls"]
         [table-views/gmail-like-pagination :pagination-custom-controls [::subs/pagination-data]]]]])]])


(defn sorting []
  [:div
   [:div
    "Sorting is enabled on per-column basis. To make column sortable just add "
    [:code.inline-code "::sorting"] " with the value "
    [:code.inline-code "{::enabled? true}"] " to particular column definition in "
    [:code.inline-code "columns-def"] " vector. "
    "In the example below, index and play_count columns are made sortable."]

   [info-message
    [:div " To sort table, click on a column header (for the column on which sorting was enabled)"]]

   [warning-message
    [:div
     " When the table was sorted by particular column, the <th> element of sorted column will have 2 HTML classes assigned: "
     [:code.inline-code "sorted-by"] " and either " [:code.inline-code "asc"] " or " [:code.inline-code "desc"]
     ". This allows to apply CSS styling to this column to emphasize that the table was sorted by it. DataTable doesn't render additional visual clues to show emphasize it."]]


   [tabs-wrapper
    :sorting
    [::subs/basic-definition-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"
      ::dt/sorting      {::dt/enabled? true}}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:play_count]
      ::dt/column-label "Play count"
      ::dt/sorting      {::dt/enabled? true}}]
    {::dt/table-classes ["ui" "very" "basic" "collapsing" "celled" "table"]}
    [{:data-tab  "css-example"
      :label     "CSS"
      :component (fn []
                   [:pre
                    [:code {:class "css"}
                     "div.re-frame-datatable > table > thead th.sorted-by:after {\n    display: inline-block;\n}\n\ndiv.re-frame-datatable > table > thead th.sorted-by.desc:after {\n    content: '\\f0d7';\n}\n\ndiv.re-frame-datatable > table > thead th.asc:after {\n    content: '\\f0d8';\n}\n\ndiv.re-frame-datatable > table > thead th:after {\n    display: none;\n    font-family: Icons;\n    margin-left: .5em;\n}"]])}]]])



(defn cell-rendering []
  [:div
   [:div
    "Each entry in " [:code.inline-code "columns-def"] " vector supports " [:code.inline-code "::render-fn"]
    " option that allows to specify function that defines Reagent component which should be used for rendering. This function should have the following signature: "
    [:pre
     [:code {:class "clojure"}
      "(defn custom-formatter [value & [item]])"]]
    [:ul
     [:li [:code.inline-code "value"] " - actual value of column property in this row"]
     [:li [:code.inline-code "item"] " - actual object in this row (can be used to pass arbitrary key-value pairs to cell rendering function)"]]]

   [:h5.ui.header "Basic custom rendering"]
   [tabs-wrapper
    :cell-rendering-basic
    [::subs/cell-rendering-data]
    [{::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:artist]
      ::dt/column-label "Artist"
      ::dt/render-fn    formatters/artist-formatter}
     {::dt/column-key   [:duration]
      ::dt/column-label "Duration"
      ::dt/render-fn    formatters/duration-formatter}
     {::dt/column-key   [:album]
      ::dt/column-label "Album"
      ::dt/render-fn    formatters/album-formatter}
     {::dt/column-key   [:rating]
      ::dt/column-label "Rating"
      ::dt/render-fn    formatters/rating-formatter}]
    {::dt/table-classes ["ui" "very" "basic" "collapsing" "celled" "table"]}
    [{:data-tab  "formatters-source"
      :label     "Formatters Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source formatters/artist-formatter))
                    (with-out-str (r/source formatters/duration-formatter))
                    (with-out-str (r/source formatters/album-formatter))
                    (with-out-str (r/source formatters/rating-formatter))])}]]

   [:h5.ui.header "Using \"item\" argument"]
   [tabs-wrapper
    :cell-rendering-advanced
    [::subs/cell-rendering-data]
    [{::dt/column-key   [:name]
      ::dt/column-label "Song"
      ::dt/render-fn    formatters/song-digest-formatter}
     {::dt/column-key   [:artist]
      ::dt/column-label "Artist"
      ::dt/render-fn    formatters/artist-formatter}]
    {::dt/table-classes ["ui" "very" "basic" "collapsing" "celled" "table"]}
    [{:data-tab  "formatters-source"
      :label     "Formatters Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source formatters/song-digest-formatter))
                    (with-out-str (r/source formatters/artist-formatter))])}]]])



(defn rows-selection []
  [:div
   [:div
    [:p
     "To enable selection, pass " [:code.inline-code "::selection"] " option with value " [:code.inline-code "{::enabled? true}"] "."]

    [:div
     "To access selected items, DataTable provides subsciprtion " [:code.inline-code "::selected-items"] ", which accepts 2 arguments"
     [:ul
      [:li [:code.inline-code "datatable-id"] " - same keyword, that was used in DataTable definition"]
      [:li [:code.inline-code "data-sub"] " - same subscription vector, that was used in DataTable definition"]]]]

   [tabs-wrapper
    :rows-selection-basic
    [::subs/basic-definition-data]
    [{::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:play_count]
      ::dt/column-label "Play count"}]
    {::dt/table-classes ["ui" "very" "basic" "collapsing" "celled" "table"]
     ::dt/selection     {::dt/enabled? true}}
    [{:data-tab  "selected-items-preview"
      :label     "Selected Items Source"
      :component (fn []
                   [formatters/formatted-code table-views/selected-rows-preview])}]
    (fn [dt-def]
      [:div.ui.two.column.divided.grid
       [:div.column
        [:h5.ui.header "Table"]
        dt-def]

       [:div.column
        [:h5.ui.header "Selected items"]
        [formatters/formatted-code
         @(re-frame/subscribe [::dt/selected-items :rows-selection-basic [::subs/basic-definition-data]])]]])]

   [:div
    [:p
     "Row selection also works with pagination and sorting. If pagination is enabled, \"select/unselect all\" will select/unselect all elements on all pages"]

    [warning-message
     [:div
      [:p
       "If you plan to modify the content of DataTable based on selection (for example, select n elements and delete them via separate handler),
       you also need to dispatch an vent that will unselect all items in DataTable.
       If you'll not do that, after deteletion some other elements will remain selected based on internal DataTable indexing mechanism."]
      [:p
       "To unselect all selected rows, dispatch the following event from the handler which modifies the content of DataTable."]
      [:pre
       [:code {:class "clojure"}
        "[::dt/unselect-all-rows datatable-id]"]]]]]

   [tabs-wrapper
    :rows-selection-pagination-sorting
    [::subs/pagination-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"
      ::dt/sorting      {::dt/enabled? true}}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"
      ::dt/sorting      {::dt/enabled? true}}
     {::dt/column-key   [:play_count]
      ::dt/column-label "Play count"
      ::dt/sorting      {::dt/enabled? true}}]
    {::dt/table-classes ["ui" "very" "basic" "collapsing" "celled" "table"]
     ::dt/selection     {::dt/enabled? true}
     ::dt/pagination    {::dt/enabled? true
                         ::dt/per-page 5}}
    nil
    (fn [dt-def]
      [:div.ui.two.column.divided.grid
       [:div.column
        [:h5.ui.header "Table"]
        [dt/default-pagination-controls :rows-selection-pagination-sorting [::subs/pagination-data]]
        dt-def]

       [:div.column
        [:h5.ui.header "Selected items"]
        [formatters/formatted-code
         @(re-frame/subscribe [::dt/selected-items :rows-selection-pagination-sorting [::subs/pagination-data]])]]])]])



(defn additional-structure []
  [:div
   [:h5.ui.header "Extra Header Row Component"]

   [:div
    "DataTable supports rendering of an additional row inside " [:code.inline-code "<thead>"]
    " (it will be rendered on top of header row, rendered from " [:code.inline-code "columns-def"] " vector). "
    "To render it, use " [:code.inline-code "::extra-header-row-component"] " option key with the valid Reagent component passed as a value."

    [warning-message
     [:div
      " Notice that DataTable will not validate that passed component is in fact valid, it just checks if it's a function."
      "Internally, it will be rendered as " [:code.inline-code "[(::extra-header-row-component options)]"]]]]

   [tabs-wrapper
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
                    (with-out-str (r/source table-views/aggregation-row))])}]]

   [:h5.ui.header "Footer Component"]

   [:div
    "DataTable can also render an arbitrary Reagent comopnent inside " [:code.inline-code "<tfoot>"] " HTML tag."
    "To render it, use " [:code.inline-code "::footer-component"] " option key with the valid Reagent component passed as a value."

    [info-message
     [:div
      "Notice that in the example below " [:code.inline-code "total-play-count-footer"] " uses additional subscription to get total play count. "
      "DataTable doesn't maintain any intermediate state of given data, so in order to access it - use the same subscription as passed to DataTable (or the one which is build on top of it)"]]]

   [tabs-wrapper
    :footer-component
    [::subs/basic-definition-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "Index"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:play_count]
      ::dt/column-label "Play count"}]
    {::dt/table-classes    ["ui" "celled" "table"]
     ::dt/footer-component table-views/total-play-count-footer}
    [{:data-tab  "footer-source"
      :label     "Footer Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source table-views/total-play-count-footer))])}]]

   [:h5.ui.header "Empty Dataset"]

   [:div
    [:p
     "In case if given subscription returns an empty datasert, DataTable will render single row with all " [:code.inline-code "<td>"]
     " elements collapsed into single one via " [:code.inline-code ":col-span"] " attribute."]
    [:p
     "If you want to provide a custom Reagent component to be rendered inside this empty row, use "
     [:code.inline-code "::empty-tbody-component"] " option."]]

   [tabs-wrapper
    :empty-tbody
    [::subs/empty-dataset]
    [{::dt/column-key   [:index]
      ::dt/column-label "Index"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:play_count]
      ::dt/column-label "Play count"}]
    {::dt/table-classes         ["ui" "celled" "table"]
     ::dt/empty-tbody-component table-views/empty-tbody-formatter
     ::dt/pagination            {::dt/enabled? true}
     ::dt/selection             {::dt/enabled? true}}
    [{:data-tab  "empty-tbody-source"
      :label     "Empty Row Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source table-views/empty-tbody-formatter))])}]

    (fn [dt-def]
      [:div.ui.grid
       [:div.row
        [:div.right.aligned.column
         [dt/default-pagination-controls :empty-tbody [::subs/empty-dataset]]]]
       [:div.row
        [:div.column
         dt-def]]])]])


(defn marking-individual-elements []
  [:div
   [:h5.ui.header "Styling Individual Cells"]

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

    [info-message
     [:div "In the example below " [:code.inline-code "row-value"] " argument is not provided to neither of functions, because the value of class can be determined by single property only."]]]

   [tabs-wrapper
    :marking-cells
    [::subs/marking-elements-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:play_count]
      ::dt/column-label "Play Count"
      ::dt/td-class-fn  table-views/play-count-td-classes}
     {::dt/column-key   [:rating]
      ::dt/column-label "Rating"
      ::dt/td-class-fn  table-views/rating-td-classes}]
    {::dt/table-classes ["ui" "celled" "table"]}
    [{:data-tab  "css-classes-source"
      :label     "CSS Classes Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source table-views/play-count-td-classes))
                    (with-out-str (r/source table-views/rating-td-classes))])}]]

   [:h5.ui.header "Styling Individual Rows"]
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

   [tabs-wrapper
    :marking-rows
    [::subs/marking-elements-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:play_count]
      ::dt/column-label "Play Count"}
     {::dt/column-key   [:rating]
      ::dt/column-label "Rating"}]
    {::dt/table-classes ["ui" "celled" "table"]
     ::dt/tr-class-fn   table-views/play-count-tr-classes}
    [{:data-tab  "css-classes-source"
      :label     "CSS Classes Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source table-views/play-count-tr-classes))])}]]])



(defn main-panel []
  (reagent/create-class
    {:component-function
     (fn []
       (let [sections [["usage" "Usage" usage-section]
                       ["basic" "Basic Definition" basic-definition]
                       ["css-options" "CSS Options" css-options]
                       ["pagination" "Pagination" pagination]
                       ["sorting" "Sorting" sorting]
                       ["cell-rendering" "Cell Custom Rendering" cell-rendering]
                       ["rows-selection" "Rows Selection" rows-selection]
                       ["additional-structure" "Additional Structure" additional-structure]
                       ["marking-individual-elements" "Marking Individual Elements" marking-individual-elements]]]

         [:div.ui.main.text.container
          [:div.ui.vertical.segment
           [:div.ui.dividing.right.rail
            [:div.ui.sticky
             {:style {:min-height "260px"}}
             [:div.ui.vertical.text.menu
              (for [[dom-id label] sections]
                ^{:key dom-id}
                [:a.item
                 {:href (str \# dom-id)}
                 label])]]]

           [:div#context
            [main-header]

            (for [[dom-id label component] sections]
              ^{:key dom-id}
              [:div.ui.section
               [:a {:id dom-id :class "anchor"}]
               [:h3.ui.dividing.header label]
               [component]])]]]))


     :component-did-mount
     (fn []
       (.ready (js/$ js/document)
               (fn []
                 (.tab (js/$ ".menu .item"))
                 (.sticky (js/$ ".ui.sticky") (clj->js {:context "#context"})))))}))

