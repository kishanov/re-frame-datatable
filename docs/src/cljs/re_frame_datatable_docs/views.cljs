(ns re-frame-datatable-docs.views
  (:require [re-frame-datatable.core :as dt]
            [re-frame-datatable-docs.subs :as subs]
            [cljs.pprint :as pp]
            [re-frame.core :as re-frame]
            [clojure.walk :as walk]
            [cljs.pprint :as pp]
            [re-frame-datatable-docs.formatters :as formatters]
            [cljs.repl :as r]
            [reagent.core :as reagent]))


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



(defn formatted-code [data]
  [:pre
   [:code {:class "clojure"}
    (with-out-str
      (pp/pprint
        (walk/postwalk
          (fn [x]
            (cond
              (fn? x)
              (let [fname (last (re-find #"^function .*\$(.*)\(" (str x)))]
                (case fname
                  "duration_formatter" (do
                                         (r/source formatters/duration-formatter)
                                         (println)
                                         'duration-formatter)

                  "album_formatter" (do
                                      (r/source formatters/album-formatter)
                                      (println)
                                      'album-formatter)

                  "rating_formatter" (do
                                       (r/source formatters/rating-formatter)
                                       (println)
                                       'rating-formatter)

                  "song_digest_formatter" (do
                                            (r/source formatters/song-digest-formatter)
                                            (println)
                                            'song-digest-formatter)

                  "artist_formatter" (do
                                       (r/source formatters/artist-formatter)
                                       (println)
                                       'artist-formatter)))


              (and (keyword? x)
                   (re-seq #"^:re-frame-datatable.core" (str x)))
              (-> x
                  (str)
                  (clojure.string/replace #"^:re-frame-datatable.core" ":dt")
                  (keyword))

              :else
              x))
          data)))]])



(defn tabs-wrapper [dt-id subscription columns-def options]
  (let [data (re-frame/subscribe subscription)
        example-dom-id (str (name dt-id) "-example")
        source-dom-id (str (name dt-id) "-source")
        data-dom-id (str (name dt-id) "-data")]

    (fn []
      (let [dt-def [dt/datatable dt-id subscription columns-def options]]
        [:div {:style {:margin-top "2em"}}
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
           (vec (cons `dt/datatable
                      (->> dt-def (rest) (filter (complement nil?)))))]]

         [:div.ui.bottom.attached.tab.segment
          {:data-tab data-dom-id}
          [formatted-code @data]]]))))



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
     ::dt/table-classes ["ui" "very" "basic" "collapsing" "celled" "table"]}]])


(defn sorting []
  [:div
   [:div
    "Sorting is enabled on per-column basis. To make column sortable just add "
    [:code.inline-code "::sorting"] " with the value "
    [:code.inline-code "{::enabled? true}"] " to particular column definition in "
    [:code.inline-code "columns-def"] " vector. "
    "In the example below, index and play_count columns are made sortable."]

   [:div.ui.info.message
    [:i.info.circle.icon]
    " To sort table, click on a column header (for the column on which sorting was enabled)"]

   [:div.ui.warning.message
    [:i.warning.sign.icon]
    " When the table was sorted by particular column, the <th> element of sorted column will have 2 HTML classes assigned: "
    [:code.inline-code "sorted-by"] " and either " [:code.inline-code "asc"] " or " [:code.inline-code "desc"]
    ". This allows to apply CSS styling to this column to emphasize that the table was sorted by it. DataTable doesn't render additional visual clues to show emphasize it."]

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
    {::dt/table-classes ["ui" "very" "basic" "collapsing" "celled" "table"]}]])



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
    :cell-rendering
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
    {::dt/table-classes ["ui" "very" "basic" "collapsing" "celled" "table"]}]

   [:h5.ui.header "Using \"item\" argument"]
   [tabs-wrapper
    :cell-rendering
    [::subs/cell-rendering-data]
    [{::dt/column-key   [:name]
      ::dt/column-label "Song"
      ::dt/render-fn    formatters/song-digest-formatter}
     {::dt/column-key   [:artist]
      ::dt/column-label "Artist"
      ::dt/render-fn    formatters/artist-formatter}]
    {::dt/table-classes ["ui" "very" "basic" "collapsing" "celled" "table"]}]])




(defn main-panel []
  (reagent/create-class
    {:component-function
     (fn []
       (let [sections [["usage" "Usage" usage-section]
                       ["basic" "Basic Definition" basic-definition]
                       ["css-options" "CSS Options" css-options]
                       ["pagination" "Pagination" pagination]
                       ["sorting" "Sorting" sorting]
                       ["cell-rendering" "Cell Custom Rendering" cell-rendering]]]

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

