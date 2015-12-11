(ns cljs-treeview.core
  (:require [reagent.core :as r]
            [cljs-treeview.tree-view :refer [tree-view]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(println (:text (deref app-state)))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn simple-example []
  [:div
   "hello"]
  [tree-view {:value {:a {:b {:c @app-state}}}}])

(defn ^:export run []
  (r/render [simple-example]
            (js/document.getElementById "app")))
