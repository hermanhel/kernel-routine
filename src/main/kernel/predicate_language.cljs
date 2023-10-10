(ns kernel.predicate-language
  (:require [cljs.reader :as reader])
  (:require [kernel.relative-clock :as rc])
  )

(defn evaluate [form rc-list]
  "evaluate the predicate-langague form's value
I may change rc-list to some environment state containing rc-list in the future, when there's more things to test"

  ;; (js/console.log (count form) rc-list "and")
  (condp = (if (coll? form) (first form) form)
    ;; 'and (apply 'clojure.core/and (map evaluate (rest form)))
    ;; apply won't work here. it does not take macro. I could make a fn there but then it can't take arbitary many argument.
    ;; could make apply work with another macro and eval.
    'and (reduce (fn [elt result] (and elt result) ) true (map #(evaluate % rc-list) (rest form)))
    'or (reduce (fn [elt result] (or elt result) ) false (map #(evaluate % rc-list) (rest form)))
    ;; 'not ;;(second form);;(apply 'clojure.core/not (evaluate (second form)))
    ;; 'not (not (evaluate (second form)))
    'a (>= (.getHours (new js/Date)) (second form))
    'r (rc/relative-clock-check rc-list (str (second form)) (nth form 2))
      ;; (do (js/console.log rc-list) true)
    true true
    false false
    nil nil
      )
  )
(defn evaluate-string [string rc-list]
  "evaluate the pl string"
  (evaluate (reader/read-string string ) rc-list)
  ;; (js/console.log rc-list)
  )
