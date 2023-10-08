(ns kernel.core
  (:require [reagent.core :as r])
  (:require [reagent.dom :as dom])
  )

(defonce current-counts (r/atom []))

(defn init []
  (println "the app has started! YES")

  )

(defn vec-delete [v i]
  (into (subvec v 0 i) (subvec v (inc i)))
  )

(defn Counter [i counter]
[:div ;;{:key (str i)}
    counter
    [:button
     {:onClick (fn [] (swap! current-counts update i inc))}
     "+1"]
    [:button
     {:onClick (fn [] (swap! current-counts update i dec))}
     "-1"]
[:button {:onClick (fn [] (swap! current-counts vec-delete i))} "X"]
    ]
  )

(defn Application []
  [:div
   [:h1 "Counter"]
   " | "
  (doall
   (for [[i counter] (map vector (range) @current-counts)]
    ^{:key (str i)} [Counter i counter]

   ))
    [:button {:onClick (fn [] (swap! current-counts conj 0))} "Add counter"]


   ]
  )
(dom/render [Application] (js/document.getElementById "app"))
