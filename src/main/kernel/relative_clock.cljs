(ns kernel.relative-clock
  (:require [kernel.util :as util])
  )

;; relatie-clock-list: looks like ''(("wakeup" 100000) ("cognitive" 123124) ("saltwater" 155515))

(defn relative-clock-add [clock-list key]
  "return a list adding (key . current-time) to the list clock-list"
  (swap! clock-list #(conj % (list key (.now js/Date))))
  )
(defn relative-clock-check
  ([clock-list key] (relative-clock-check clock-list key 0))
  ([clock-list key distance]
  "return t if key is present in clock-list and now - the corresponding time > distance; nil otherwise

see if the distance have been reached; semantic: relative-clock-check clock-list wakeup 60000 = have it been 60000ms since wakeup?"
   ;; (js/console.log (first  (filter #(= key (first %)) @clock-list)))
;;(- (.now js/Date) (util/find-first #(= key (first %)) @clock-list ))
   (if (first (filter #(= key (first %)) @clock-list))
     (> (- (.now js/Date) (second (util/find-first #(= key (first %)) @clock-list))) distance)
   nil
   )
  )
  )
