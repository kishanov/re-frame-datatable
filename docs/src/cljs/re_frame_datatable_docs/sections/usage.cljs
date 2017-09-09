(ns re-frame-datatable-docs.sections.usage
  (:require [re-frame-datatable-docs.components :as components]
            [re-frame-datatable-docs.formatters :as formatters]
            [re-frame-datatable-docs.table-views :as table-views]

            [re-frame-datatable-docs.subs :as subs]
            [re-frame-datatable.core :as dt]

            [cljs.repl :as r]))



(defn definition []
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
   options] ; optional map of additional options)"]]

   [components/info-message
    [:p "A complete example is available at "
     [:a {:href "https://kishanov.github.io/re-frame-datatable-example/"}
      "https://kishanov.github.io/re-frame-datatable-example/"]]]])



(defn basic-usage []
  [:div
   [:div
    [:p
     "There is only 1 mandatory parameter that should be provided for each map in "
     [:code.inline-code "columns-def"] " vector:"]
    [:ul.ui.list
     [:li [:code.inline-code "::column-key"] " - a vector that is used to access value of each item via " [:code "get-in"]]]

    [:p
     "To specify the label for the column (the one that will be rendered for a given column inside " [:code.inline-code "<th>"] " element of header row)"
     ", use " [:code.inline-code "::column-label"] " parameter. It can be one of the following:"]

    [:ul.ui.list
     [:li "A string, that will be rendered as is"]
     [:li "A Reagent component"]]]

   [components/tabs-wrapper
    :basic-definition
    [::subs/basic-definition-data]
    [{::dt/column-key [:index]}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}
     {::dt/column-key   [:stats :play_count]
      ::dt/column-label formatters/play-count-th}]
    nil
    [{:data-tab  "th-fourammter-source"
      :label     "Play Count Formatter Source"
      :component (fn []
                   [formatters/formatted-function-def
                    (with-out-str (r/source formatters/play-count-th))])}]]])






