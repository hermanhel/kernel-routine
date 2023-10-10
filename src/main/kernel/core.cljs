(ns kernel.core
  (:require [reagent.core :as r])
  (:require [reagent.dom :as dom])
  (:require [kernel.event :as event])
  (:require [kernel.ui :as ui])
  (:require [kernel.eval :as eval])
  (:require [kernel.util :as util])
  (:require [kernel.relative-clock :as rc])
  (:require [cljs.js :as cljs])
  (:require [cljs.reader :as reader])
  )

(defonce current-counts (r/atom []))

(defn Counter [i counter]
[:div ;;{:key (str i)}
    counter
    [:button
     {:onClick (fn [] (swap! current-counts update i inc))}
     "+1"]
    [:button
     {:onClick (fn [] (swap! current-counts update i dec))}
     "-1"]
    ]
  )


(defonce Events event/routine)
(defonce schedule
  (r/atom (doall (event/schedulize-routine event/routine)))
  )

(defonce relative-clock-list (r/atom (list (list "" 0))))


(defonce timers (r/atom {})) ;; the timer used in kernel is the kernel timer TODO one thing is that do I give each timer(kernel, boosters, cooldown...) a seperate interval, or do I get all timer to work in on "game-loop".

(defn timer-on-event [key]
  "count up every second. return a id that is used by js/clearInterval to stop the timer"
  (js/setInterval
   (fn []
    (swap! schedule
           #(map
             (fn [event]
               (if (= key (:key event))
                 (update-in event [:time] inc)
                 event
                 )
               )
             %)
           )
     )
   1000)
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
(defonce timer (r/atom 0))

(defn big-loop []
  "game-loop-like loop, where all timer-based features are updated, like cooldown, booster cooldown, timer strings, progress animations..."
  (swap! timer inc)
  (swap-each-p
   schedule
   (fn [event] (:current? event))
               #(update-in % [:time] inc)
               )
  )

(defonce big-loop-interval
  (js/setInterval big-loop 1000))




;; (.setItem (.-localStorage js/window) "herman" (str {:key (fn [] true)}))
;; I translated https://stackoverflow.com/questions/22545031/store-a-function-in-indexeddb-datastore into cljs



(set-item! "herman" {:key (fn [] true)})
(defn Application []

  [:div {:class "bg-white dark:bg-gray-700 h-full"}
   [ui/Panel @schedule relative-clock-list kernel-event-on-click]
   ;; [Bottom-nav-bar]
   [ui/Bottom-Label]
   ]
  )

(dom/render [Application] (js/document.getElementById "app"))
