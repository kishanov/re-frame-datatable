(ns re-frame-datatable-docs.sections.cell-rendering
  (:require [re-frame-datatable-docs.components :as components]
            [re-frame-datatable-docs.formatters :as formatters]
            [re-frame-datatable-docs.table-views :as table-views]
            [cljs.repl :as r]

            [re-frame-datatable-docs.subs :as subs]
            [re-frame-datatable.core :as dt]
            [re-frame-datatable.views :as dt-views]))



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
   [components/tabs-wrapper
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
     {::dt/column-key   [:stats :rating]
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
                    (with-out-str (r/source formatters/rating-formatter))])}]]])



(defn using-item-argument []
  [:div
   [components/tabs-wrapper
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
