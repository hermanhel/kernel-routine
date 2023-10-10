(ns kernel.core
  (:require [reagent.core :as r])
  (:require [cljs.reader :as reader])
  (:require [reagent.dom :as dom])
  (:require [kernel.event :as event])
  (:require [kernel.predicate-language :as pl])
  (:require [kernel.ui :as ui])
  (:require [kernel.eval :as eval])
  (:require [kernel.util :as util])
  (:require [kernel.relative-clock :as rc])
  (:require [cljs.js :as cljs])
  (:require [kernel.db :as db ])
  (:require [clojure.string :as cs])
  (:require [promesa.core :as p])
  )


(defonce schedule
  (r/atom (doall (event/schedulize-routine event/routine)))
  )

(defonce relative-clock-list (r/atom (list (list "" 0))))

(defn hard-reset []
     (reset! relative-clock-list '())
     (reset! schedule (doall (event/schedulize-routine event/routine)))
     (db/assoc-local "schedule" schedule)
     (db/assoc-local "relative-clock-list" relative-clock-list)
  )


;; TODO write a wrapper to update the schedule base on a key. `timer-on-event` and `kernel-event-on-click` have a part that is really similar
(defn kernel-event-on-click [key]
  "when event key is clicked, do this on EVERY event in the routine"
  (swap! schedule
         ;; `schedule` is passed to the following map sexp
         ;; the following code
         #(map
           (fn [event]
             ;; TODO rewrite this with cond
             (if (= key (:key event))
             ;; being the clicked event
               (if (:current? event)
                 ;; being the current event - discard
                 (do
                   (rc/relative-clock-add relative-clock-list (:key event))
                   (db/assoc-local "relative-clock-list" relative-clock-list)
                   (update-in event [:discarded?] not)
                   ) ;; if it is clickable, discarded? is false
                 ;; not being the current event - now you are NOT not
                 (update-in event [:current?] not)
                 )
               ;; not being the clicked event
               (if (:current? event)
                 ;; is current - now you're not
                 (update-in event [:current?] not)
               ;; is not current - return the event as is
                 event
                 )
               )
             )
           %
           )
         )
  (db/assoc-local "schedule" schedule)
  (when (= key "sleep")
    (hard-reset)    )
  ;; (.setItem db "schedule" (pr-str @schedule) (fn [_ val] (js/console.log "set value:" _ val)))
  ;; (.setItem db "" (pr-str @schedule) (fn [_ val] (js/console.log "set value:" _ val)))


  )


(defn swap-each [atom-coll fn]
  "map fn on every 1-level item of atom-coll. fn take 1 argument that is the 1-level item
usage: (swap-each schedule (update-in [:current true])) would change every first level item's :current value to true"
 (swap! atom-coll
        #(map
          fn
          %
          )
        )
  )

(defn swap-each-p [atom-coll p update-fn]
  "apply update-fn to item if predicate p return ture. both p and update-fn take 1 argument that is the 1st-level element"
  (swap-each atom-coll (fn [event] (if (p event) (update-fn event) event)))
  )

(defn swap-only-key [atom-coll key update-fn]
  "if the item have :key value that is the same as KEY, apply update-fn.

useful because react require :key, so I have one for each of the event, and I do identify them with it."
  )

(defn big-loop []
  "game-loop-like loop, where all timer-based features are updated, like cooldown, booster cooldown, timer strings, progress animations..."
  (swap-each-p
   schedule
   (fn [event] (:current? event))
               #(update-in % [:time] inc)
               )
  (db/assoc-local "schedule" schedule)
  ;; (.setItem db "schedule" (pr-str @schedule) (fn [_ val]
                                               ;; (js/console.log "set value:" _ val)
                                                ;; ))
  )

(defonce big-loop-interval
  (js/setInterval big-loop 1000))




;; (.setItem (.-localStorage js/window) "herman" (str {:key (fn [] true)}))
;; I translated https://stackoverflow.com/questions/22545031/store-a-function-in-indexeddb-datastore into cljs



(defn init []
  "executed after refresh"
  ;; (.getItem db "schedule" (fn [_ val]
  ;;                           (reset! schedule (reader/read-string val))
  ;;                           ))

  (db/get-local "schedule" schedule)
  (db/get-local "relative-clock-list" relative-clock-list)

  )
(defn Application []

  [:div {:class "bg-white dark:bg-gray-700 h-full"}
   ;; (keyword schedule)
   ;; @schedule
   [ui/Panel @schedule relative-clock-list kernel-event-on-click]
   ;; [Bottom-nav-bar]
   [ui/Bottom-Label hard-reset]
   ]
  )

(dom/render [Application] (js/document.getElementById "app"))
