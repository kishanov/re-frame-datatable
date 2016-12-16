(ns re-frame-datatable-docs.views
  (:require [re-frame-datatable.core :as dt]
            [re-frame-datatable-docs.subs :as subs]
            [cljs.pprint :as pp]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))


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



(defn formatted-code [data]
  [:pre
   [:code {:class "clojure"}
    (with-out-str (pp/pprint data))]])



(defn tabs-wrapper [dt-id subscription columns-def options]
  (let [data (re-frame/subscribe subscription)
        example-dom-id (str (name dt-id) "-example")
        source-dom-id (str (name dt-id) "-source")
        data-dom-id (str (name dt-id) "-data")]

    (fn []
      (let [dt-def [dt/datatable dt-id subscription columns-def options]]
        [:div
         [:div.ui.top.attached.tabular.menu
          [:a.active.item
           {:data-tab example-dom-id} "Example"]
          [:a.item
           {:data-tab source-dom-id} "Source"]
          [:a.item
           {:data-tab data-dom-id} "Data"]]

         [:div.ui.bottom.attached.active.tab.segment
          {:data-tab example-dom-id}
          dt-def]

         [:div.ui.bottom.attached.tab.segment
          {:data-tab source-dom-id}
          [formatted-code
           (vec (cons `re-frame-datatable.core/datatable
                      (->> dt-def (rest) (filter (complement nil?)))))]]

         [:div.ui.bottom.attached.tab.segment
          {:data-tab data-dom-id}
          [formatted-code @data]]]))))



(defn usage-section []
  [:div.ui.section
   [:h3.ui.dividing.header "Usage"]
   [:p "re-frame-datatable should be used as any other Reagent component. First, require it in the file that contains your re-frame application views:"]
   [:pre
    [:code {:class "clojure"}
     "(:require [re-frame-datatable.core :as dt])"]]

   [:p "Then define a Reagent component which uses a datatable:"]

   [:pre
    [:code {:class "clojure"}
     "(defn my-component []
     datatable-key ; a keyword, that will be used in re-frame's `app-db` to store datatable state
     subscription-vec ; a vector, which will be used by datatable to render data via `(re-frame/subscribe subscription-vec)`
     columns-def ; a vector of maps, that defines how each column of datatable should look like
     options ; optional map of additional options)"]]])


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
  [:div.ui.section
   [:h3.ui.dividing.header "Basic definition"]
   [:p
    "There are only 2 mandatory definitions that should be provided for each map in "
    [:code.inline-code "columns-def"] " vector:"
    [:ul.ui.list
     [:li [:code.inline-code "::column-key"] " - a vector that is used to access value of each item via " [:code "get-in"]]
     [:li [:code.inline-code "::column-label"] " - a string that will be a header for a column"]]]
   [tabs-wrapper
    :basic-definition
    [::subs/songs-list]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}]]])


(defn css-options []
  [:div.ui.section
   [:h3.ui.dividing.header "CSS options"]
   [:p
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
   [:p "To provide CSS classes that should be applied to <table> tag, use "
    [:code.inline-code "::table-classes"] " key in options as shown in example. Further styling can be provided via CSS selectors:"
    [:pre
     [:code {:class "css"}
      ".ui.table > thead > th { color: red; } "]]]

   [tabs-wrapper
    :css-options
    [::subs/songs-list]
    [{::dt/column-key   [:index]
      ::dt/column-label "#"}
     {::dt/column-key   [:name]
      ::dt/column-label "Name"}]
    {::dt/table-classes ["ui" "celled" "stripped" "table"]}]])



(defn main-panel []
  (reagent/create-class
    {:component-function
     (fn []
       [:div.ui.main.text.container
        [main-header]

        [usage-section]

        [basic-definition]

        [css-options]])


     :component-did-mount
     (fn []
       (.tab (js/$ ".menu .item")))}))
