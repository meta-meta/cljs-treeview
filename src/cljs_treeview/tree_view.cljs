(ns cljs-treeview.tree-view
  (:require [clojure.string :as string :refer [join]]
            [goog.Uri]
            [reagent.core :as reagent]
            [reagent.impl.template]))


(defprotocol LeafNode
  (render-leaf-node [val param]))

(extend-type default
  LeafNode
  (render-leaf-node [val _]
    [:code (pr-str val)]))



(defn- render-node [label child props]
  (let [{:keys [default-collapsed]} props]
    ;; js/TreeView is a native React component.  Child is a Reagent hiccup vector,
    ;; so we need to translate that into a React component using Reagent internals
    ;; before passing that to react.
    (js/TreeView #js {:nodeLabel (pr-str label)
                      :defaultCollapsed default-collapsed}
                 (reagent.impl.template/as-element child))))


(defn- render-leaf-kv [label child]
  "Don't wrap the leaf nodes in a TreeView"
  [:div (pr-str label) "\u00a0" child])


(defn indexed [xs]
  (into {} (keep-indexed (fn [ix o] [ix o]) xs)))


(defn- render-editor [tree-node props]
  ;; This eventually gets parsed as Reagent hiccup syntax, so we must take care
  ;; not to use vectors as lists
  (cond
    (map? tree-node)
    (reduce (fn [acc [k v]]
              (if (coll? v)
                (conj acc (render-node k (render-editor v props) props))
                (conj acc (render-leaf-kv k (render-leaf-node v props)))))
            '() ; the native DOM is nested lists
            tree-node)

    (or
      (seq? tree-node)
      (vector? tree-node)
      (array? tree-node)
      (set? tree-node))
    (render-editor (indexed tree-node) props)

    :at-leaf-node
    (do
      (println "unsupported data type: " (type tree-node))
      (assert false "unsupported data type, see console"))
    #_ [:h1 tree-node]
    #_ (render-leaf-node tree-node props)))


(def default-props {:default-collapsed false})

(defn tree-view
  "Render a collapsable tree view for an EDN tree. Takes an optional param
    to pass along to each leaf nodes render function."
  [props]
  (let [{:keys [value] :as props} (merge default-props props)]
    [:div.tree-view (render-editor value props)]))
