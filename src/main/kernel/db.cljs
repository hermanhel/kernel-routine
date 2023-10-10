(ns kernel.db
  (:require [localforage :as ldb])
  (:require [cljs.reader :as reader])
  )

(defn assoc-local [key val]
  "save atom val to localForage's key"
  (.setItem ldb key (pr-str @val) (fn [_ _]))
  )

(defn get-local [key target]
  "update atom val to key's value in localForage"
  (.getItem ldb key (fn [_ val] (reset! target (reader/read-string val))))
  )
