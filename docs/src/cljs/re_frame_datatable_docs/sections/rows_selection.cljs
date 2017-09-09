(ns re-frame-datatable-docs.sections.rows-selection
  (:require [re-frame-datatable-docs.components :as components]
            [re-frame-datatable-docs.formatters :as formatters]
            [re-frame-datatable-docs.table-views :as table-views]
            [cljs.repl :as r]

            [re-frame-datatable-docs.subs :as subs]
            [re-frame-datatable.core :as dt]
            [re-frame-datatable.views :as dt-views]
            [re-frame.core :as re-frame]))


(defn enable-rows-selection []
  [:div
   [:div
    [:p
     "To enable selection, pass " [:code.inline-code "::selection"] " option with value " [:code.inline-code "{::enabled? true}"] "."]

    [:div
     "To access selected items, DataTable provides subsciprtion " [:code.inline-code "::selected-items"] ", which accepts 2 arguments"
     [:ul
      [:li [:code.inline-code "datatable-id"] " - same keyword, that was used in DataTable definition"]
      [:li [:code.inline-code "data-sub"] " - same subscription vector, that was used in DataTable definition"]]]]

   [components/tabs-wrapper
    :rows-selection-basic
    [::subs/basic-definition-data]
    [{::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:stats :play_count]
      ::dt/column-label "Play count"}]
    {::dt/table-classes ["ui" "very" "basic" "collapsing" "celled" "table"]
     ::dt/selection     {::dt/enabled? true}}
    [{:data-tab  "selected-items-preview"
      :label     "Selected Items Source"
      :component (fn []
                   [:pre
                    [:code {:class "clojure"}
                     (with-out-str (r/source table-views/selected-rows-preview))]])}]
    (fn [dt-def]
      [:div.ui.two.column.divided.grid
       [:div.column
        [:h5.ui.header "Table"]
        dt-def]

       [:div.column
        [:h5.ui.header "Selected items"]
        [formatters/formatted-code
         @(re-frame/subscribe [::dt/selected-items :rows-selection-basic [::subs/basic-definition-data]])]]])]])



(defn selection-and-other-options []
  [:div
   [:div
    [:p
     "Row selection also works with pagination and sorting. If pagination is enabled, \"select/unselect all\" will select/unselect all elements on all pages"]

    [components/warning-message
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

   [components/tabs-wrapper
    :rows-selection-pagination-sorting
    [::subs/pagination-data]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"
      ::dt/sorting      {::dt/enabled? true}}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"
      ::dt/sorting      {::dt/enabled? true}}
     {::dt/column-key   [:stats :play_count]
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
        [dt-views/default-pagination-controls :rows-selection-pagination-sorting [::subs/pagination-data]]
        dt-def]

       [:div.column
        [:h5.ui.header "Selected items"]
        [formatters/formatted-code
         @(re-frame/subscribe [::dt/selected-items :rows-selection-pagination-sorting [::subs/pagination-data]])]]])]])
