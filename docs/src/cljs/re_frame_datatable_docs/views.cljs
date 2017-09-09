(ns re-frame-datatable-docs.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [cljs.repl :as r]

            [re-frame-datatable.core :as dt]
            [re-frame-datatable.views :as dt-views]

            [re-frame-datatable-docs.subs :as subs]
            [re-frame-datatable-docs.events :as events]
            [re-frame-datatable-docs.formatters :as formatters]
            [re-frame-datatable-docs.table-views :as table-views]
            [re-frame-datatable-docs.components :as components]

            [re-frame-datatable-docs.sections.usage :as usage-sections]
            [re-frame-datatable-docs.sections.styling :as styling-sections]
            [re-frame-datatable-docs.sections.additional-structure :as structure-sections]
            [re-frame-datatable-docs.sections.sorting :as sorting-sections]
            [re-frame-datatable-docs.sections.pagination :as pagination-sections]
            [re-frame-datatable-docs.sections.cell-rendering :as cell-rendering-sections]
            [re-frame-datatable-docs.sections.rows-selection :as rows-selection-sections]))



(defn sneak-peek-for-readme []
  [dt/datatable
   :songs
   [::subs/songs-list]
   [{::dt/column-key [:index]
     ::dt/sorting    {::dt/enabled? true}}
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




(defn main-panel []
  (let [active-section (re-frame/subscribe [::subs/active-section])
        doc-structure (list
                        {:group    "Usage"
                         :sections [["definition" "Definition" usage-sections/definition]
                                    ["basic-usage" "Basic Usage" usage-sections/basic-usage]]}

                        {:group    "Styling"
                         :sections [["css-options" "CSS Options" styling-sections/css-options]
                                    ["css-styling-cells" "Styling Individual Cells" styling-sections/styling-cells]
                                    ["css-styling-rows" "Styling Rows" styling-sections/styling-rows]]}

                        {:group    "Structure"
                         :sections [["no-header" "Remove Header" structure-sections/disable-header]
                                    ["extra-header-row" "Extra Header Row" structure-sections/extra-header]
                                    ["structure-footer" "Footer" structure-sections/footer]
                                    ["empty-dataset" "Empty Dataset" structure-sections/empty-dataset]]}

                        {:group    "Sorting"
                         :sections [["sorting-basic" "Enabling Sorting" sorting-sections/enable-sorting]
                                    ["sorting-custom-comp-fn" "Custom Comparator" sorting-sections/custom-sorting-fn]]}

                        {:group    "Pagination"
                         :sections [["enabling-pagination" "Enabling Pagination" pagination-sections/enabling-pagination]
                                    ["per-page-selector" "Setting Page Size" pagination-sections/per-page-selector]
                                    ["custom-pagination-controls" "Custom Pagination Controls" pagination-sections/custom-pagination-controls]]}

                        {:group    "Custom Cell Rendering"
                         :sections [["custom-cell-rendering-formatter" "Custom Formatters" cell-rendering-sections/cell-rendering]
                                    ["custom-cell-rendering-extra-arg" "Row-aware Formatters" cell-rendering-sections/using-item-argument]]}

                        {:group    "Selection (rows)"
                         :sections [["enabling-rows-selection" "Enabling Selection" rows-selection-sections/enable-rows-selection]
                                    ["rows-selection-and-other-options" "Selection and Other Options" rows-selection-sections/selection-and-other-options]]})]


    (reagent/create-class
      {:component-function
       (fn []
         [:div.ui.main.text.container
          [:div.ui.vertical.segment
           [:div.ui.dividing.right.rail
            [:div.ui.sticky
             {:style {:min-height "260px"}}
             [:div.ui.vertical.text.menu
              {:style {:margin-top "1em" :margin-bottom "2em"}}

              (doall
                (for [[i {:keys [group sections]}] (map-indexed vector doc-structure)]
                  ^{:key i}
                  [:div
                   {:style {:margin-bottom "1em"}}
                   [:h4.ui.dividing.header group]

                   (doall
                     (for [[dom-id label] sections]
                       ^{:key dom-id}
                       [:a.item
                        {:href     (str \# dom-id)
                         :class    (when (= @active-section dom-id) "active")
                         :on-click #(re-frame/dispatch [::events/set-active-section dom-id])}
                        label]))]))]]]

           [:div#context
            [main-header]

            (doall
              (for [[i {:keys [group sections]}] (map-indexed vector doc-structure)]
                ^{:key i}
                [:div
                 [:h2.ui.dividing.header group]

                 (doall
                   (for [[dom-id label component] sections]
                     ^{:key dom-id}
                     [:div.ui.section
                      [:a {:id dom-id :class "anchor"}]
                      [:h3.ui.dividing.header label]
                      [component]]))]))]]])


       :component-did-mount
       (fn []
         (.ready (js/$ js/document)
                 (fn []
                   (.tab (js/$ ".menu .item"))
                   (.sticky (js/$ ".ui.sticky") (clj->js {:context "#context"})))))})))
