(ns re-frame-datatable-docs.sections.pagination
  (:require [re-frame-datatable-docs.components :as components]
            [re-frame-datatable-docs.formatters :as formatters]
            [re-frame-datatable-docs.table-views :as table-views]
            [cljs.repl :as r]

            [re-frame-datatable-docs.subs :as subs]
            [re-frame-datatable.core :as dt]
            [re-frame-datatable.views :as dt-views]))



(defn enabling-pagination []
  (let [dt-key :pagination-basic
        dt-sub [::subs/pagination-data]]

    [:div
     [:div
      "Pagination can be enabled via " [:code.inline-code "::pagination"] " key. There are 2 options:"
      [:ul.ui.list
       [:li [:code.inline-code "::enabled?"] " - boolean to define if pagination should be enabled"]
       [:li [:code.inline-code "::per-page"] " - integer to define how many elements should be shown per page (default is 10)"]]]

     [components/warning-message
      [:div
       [:p
        "DataTable's pagination controls are not rendered together with DataTable. There are 2 reasons for it:"]
       [:ul
        [:li "DataTable is not opinionated about where to put pagination controls"]
        [:li "DataTable is not opinionated how pagination controls will look like"]]]
      "Important"]

     [:p
      "DataTable ships with additional component " [:code.inline-code "default-pagination-controls"]
      ", that accepts 2 arguments: " [:code.inline-code "datatable-key"] " and " [:code.inline-code "data-sub"]
      " which should be the same as passed to " [:code.inline-code "datatable"] " component itself"]

     [components/tabs-wrapper
      dt-key
      dt-sub
      [{::dt/column-key   [:index]
        ::dt/column-label "#"}
       {::dt/column-key   [:name]
        ::dt/column-label "Name"}
       {::dt/column-key   [:stats :play_count]
        ::dt/column-label "Play count"}]
      {::dt/pagination    {::dt/enabled? true
                           ::dt/per-page 5}
       ::dt/table-classes ["ui" "table"]}
      [{:data-tab  "default-pagination-controls-usage"
        :label     "Default Pagination Controls Usage"
        :component (fn []
                     [:pre
                      [:code {:class "clojure"}
                       (str
                         "[dt-views/default-pagination-controls "
                         dt-key
                         \space
                         dt-sub)]])}]
      (fn [dt-def]
        [:div.ui.grid
         [:div.row
          [:div.right.aligned.column
           [dt-views/default-pagination-controls dt-key dt-sub]]]
         [:div.row
          [:div.column
           dt-def]]
         [:div.row
          [:div.right.aligned.column
           [dt-views/default-pagination-controls dt-key dt-sub]]]])]]))



(defn custom-pagination-controls []
  (let [dt-key :pagination-custom-controls
        dt-sub [::subs/pagination-data]]

    [:div
     [:div
      [:p
       "It is possible to build your own custom pagiation controls. DataTable exposes single subscription - " [:code.inline-code "::pagination-state"]
       " - that accepts 2 arguments: " [:code.inline-code "datatable-key"] " and " [:code.inline-code "data-sub"]
       " That subscription returns a map with following keys:"]
      [:ul
       [:li [:code.inline-code "::cur-page"] " - an integer, which represents current page (0-based indexing)"]
       [:li [:code.inline-code "::per-page"] " - an integer, which represents maximum count of elements per each page"]
       [:li [:code.inline-code "::pages"] " - a vector of tuples of length " [:code.inline-code "::per-page"]
        ", each tuple has 2 numbers for each page: " [:code.inline-code "[first-element-index last-element-index]"] " (indexing is zero based)"
        "For example, " [:code.inline-code "[[0 4][5 9][10 12]"]]]
      [:p "See the example below to see how 2 custom pagination components are defined. It's easier to copy and modify their source, then to properly document how they work :)"]]

     [components/tabs-wrapper
      dt-key
      dt-sub
      [{::dt/column-key   [:index]
        ::dt/column-label "#"}
       {::dt/column-key   [:name]
        ::dt/column-label "Name"}
       {::dt/column-key   [:stats :play_count]
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
           [table-views/basic-pagination dt-key dt-sub]]

          [:div {:style {:margin-bottom "1em"}}
           [:h5.ui.herader "GMAIL-like pagination controls"]
           [table-views/gmail-like-pagination dt-key dt-sub]]

          [:div {:style {:margin-bottom "1em"}}
           [:h5.ui.herader "Radio Per Page Selector"]
           [table-views/per-page-radio-selector dt-key dt-sub]]]])]]))



(defn per-page-selector []
  [:div
   [:div
    "Per page count can be change via special event: " [:code.inline-code "::set-per-page-value"]]

   [components/info-message
    [:div
     [:p
      "Changing " [:code.inline-code "::per-page"] " value via event will also set the currnet page to 0"]]]

   (let [dt-key :per-page-selector-example
         dt-sub [::subs/pagination-data]]

     [components/tabs-wrapper
      dt-key
      dt-sub
      [{::dt/column-key   [:index]
        ::dt/column-label "#"}
       {::dt/column-key   [:name]
        ::dt/column-label "Name"}
       {::dt/column-key   [:stats :play_count]
        ::dt/column-label "Play count"}]
      {::dt/pagination    {::dt/enabled? true
                           ::dt/per-page 5}
       ::dt/table-classes ["ui" "table"]}
      [{:data-tab  "default-per-page-controls-usage"
        :label     "Default Per Page Usage"
        :component (fn []
                     [:pre
                      [:code {:class "clojure"}
                       (str
                         "[dt-views/per-page-selector "
                         dt-key
                         \space
                         dt-sub)]])}]
      (fn [dt-def]
        [:div.ui.grid
         [:div.row
          [:div.right.aligned.column
           [dt-views/per-page-selector dt-key dt-sub]
           [:span {:style {:min-width "1em"}}]
           [dt-views/default-pagination-controls dt-key dt-sub]]]
         [:div.row
          [:div.column
           dt-def]]])])])
