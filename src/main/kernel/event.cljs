(ns ^{:author "Linfeng"
      :doc "Event data model"}
    kernel.event
  (:require [kernel.relative-clock :as rc])
  (:require [kernel.util :as util])
  )
(def routine
  "a list of Events. Now it is hardcoded, in future it should be read from file or storage"
  (list
    {:key "wakeup" :title "Wake Up" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] true)
     :duration "5m" :cooldown -1 :discarding []}
    {:key "light" :title "Bright Light Viewing" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (rc/relative-clock-check rc-list "wakeup"))
     :duration "20m" :cooldown -1 :discarding []}
    {:key "salt-water" :title "Salt Water Intake" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (rc/relative-clock-check rc-list "wakeup"))
     :duration "5m" :cooldown -1 :discarding []}
    {:key "cognitive" :title "Cognitively Hard Thing" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] true (rc/relative-clock-check rc-list "light"))
     :duration "90m" :cooldown -1 :discarding []}
    {:key "caffeine" :title "Caffeine Intake" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (rc/relative-clock-check rc-list "wakeup" (* 90 60)) )
     :duration "5m" :cooldown -1 :discarding []}
    {:key "workout-1" :title "Workout" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (rc/relative-clock-check rc-list "wakeup" ) )
     :duration "40m" :cooldown -1 :discarding []}
    {:key "workout-2" :title "Workout" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (rc/relative-clock-check rc-list "cognitive" ))
     :duration "40m" :cooldown -1 :discarding []}
    {:key "lunch-1" :title "Lunch" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (or
                                                                                                    (rc/relative-clock-check rc-list "workout-2" )
                                                                                                    (rc/relative-clock-check rc-list "workout-1" )))
     :duration "5m" :cooldown -1 :discarding []}
    {:key "lunch-2" :title "Lunch" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (>= (util/get-hours) 12) )
     :duration "5m" :cooldown -1 :discarding []}
    {:key "chore" :title "Chore Work" :description "" :trigger-text "nil" :trigger-func (fn [rc-list]
                                                                                          (or
                                                                                           (rc/relative-clock-check rc-list "lunch-2" )
                                                                                           (rc/relative-clock-check rc-list "lunch-1" ))
                                                                                          )
     :duration "5m" :cooldown -1 :discarding []}
    {:key "nap" :title "Nap/NSDR" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (rc/relative-clock-check rc-list "chore" ))
     :duration "5m" :cooldown -1 :discarding []}
    {:key "focus-1-1" :title "Focus Work Session 1" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (rc/relative-clock-check rc-list "nap" ))
     :duration "5m" :cooldown -1 :discarding []}
    {:key "focus-1-2" :title "Focus Work Session 1" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (>= (util/get-hours) 14))
     :duration "5m" :cooldown -1 :discarding []}
    {:key "defocus-1" :title "Defocus Session 1" :description "" :trigger-text "nil" :trigger-func (fn [rc-list]
                                                                                                     (or
                                                                                                      (rc/relative-clock-check rc-list "focus-1-2" )
                                                                                                      (rc/relative-clock-check rc-list "focus-1-1" )))
     :duration "5m" :cooldown -1 :discarding []}
    {:key "focus-2" :title "Focus Work Session 2" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (rc/relative-clock-check rc-list "defocus-1" ))
     :duration "5m" :cooldown -1 :discarding []}
    {:key "defocus-2" :title "Defocus Session 2" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (rc/relative-clock-check rc-list "focus-2" ))
     :duration "5m" :cooldown -1 :discarding []}
    {:key "dinner" :title "Dinner" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (>= (util/get-hours) 18))
     :duration "5m" :cooldown -1 :discarding []}
    {:key "free" :title "Free Time" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (rc/relative-clock-check rc-list "dinner" ))
     :duration "5m" :cooldown -1 :discarding []}
    {:key "bed-prepare" :title "Prepare for Bed" :description "" :trigger-text "nil" :trigger-func (fn [rc-list] (>= (util/get-hours) 21) )
     :duration "5m" :cooldown -1 :discarding []}
    {:key "sleep" :title "Sleep" :description "" :trigger-text "nil" :trigger-func (fn [rc-list]
                                                                                     (or
                                                                                      (rc/relative-clock-check rc-list "bed-prepare")
                                                                                      (>= (util/get-hours) 22)))
     :duration "5m" :cooldown -1 :discarding []}
    ))
(defn schedulize-routine [routine]
  "routine does not store information on current and discarded, as they only make sense in the day.

    return a list of Events, each have current? and discarded? initialized to nil"
  (map (fn [event] (assoc event :current? nil :discarded? nil :time 0)) routine)
  )
