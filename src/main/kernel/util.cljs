(ns kernel.util)

(defn find-first [f col]
  (first (filter f col))
  )
(defn get-hours []
   (.getHours (new js/Date))
  )

(defn get-minutes []
   (.getMinutes (new js/Date))
  )
