(ns ^{:author "Linfeng"
      :doc "Event data model"}
    kernel.event
  (:require [kernel.relative-clock :as rc])
  (:require [kernel.util :as util])
  )
(def routine
  "a list of Events. Now it is hardcoded, in future it should be read from file or storage"
  (list
    {:key "wakeup" :title "Wake Up" :description "" :trigger-text "nil" :trigger-func "(a 0)"
     :duration "5m" :cooldown -1 :discarding []}
    {:key "light" :title "Bright Light Viewing" :description "" :trigger-text "Wake Up" :trigger-func "(r wakeup 0)"
     :duration "20m" :cooldown -1 :discarding []}
    {:key "salt-water" :title "Salt Water Intake" :description "" :trigger-text "Wake Up" :trigger-func "(r wakeup 0)"
     :duration "5m" :cooldown -1 :discarding []}
    {:key "cognitive" :title "Cognitively Hard Thing" :description "" :trigger-text "Bright Light Viewing" :trigger-func "(r light 0)"
     :duration "90m" :cooldown -1 :discarding []}
    {:key "caffeine" :title "Caffeine Intake" :description "" :trigger-text "Wake Up +90m" :trigger-func "(r wakup 5400)"
     :duration "5m" :cooldown -1 :discarding []}
    {:key "workout-1" :title "Workout" :description "" :trigger-text "Wake Up" :trigger-func "(r wakeup 0)"
     :duration "40m" :cooldown -1 :discarding []}
    {:key "workout-2" :title "Workout" :description "" :trigger-text "Cognitive Hard Thing" :trigger-func "(r cognitive 0)"
     :duration "40m" :cooldown -1 :discarding []}
    {:key "lunch-1" :title "Lunch" :description "" :trigger-text "Work Out" :trigger-func "(or (r workout-2 0) (r workout-1 0))"
     :duration "40m" :cooldown -1 :discarding []}
    {:key "lunch-2" :title "Lunch" :description "" :trigger-text "12-ish" :trigger-func "(a 12)"
     :duration "40m" :cooldown -1 :discarding []}
    {:key "chore" :title "Chore Work" :description "" :trigger-text "Lunch" :trigger-func
                                                                                          "(or
                                                                                           (r lunch-2 0)
                                                                                           (r lunch-1 0))"

     :duration "60m" :cooldown -1 :discarding []}
    {:key "nap" :title "Nap/NSDR" :description "" :trigger-text "Chore" :trigger-func "(r chore 0)"
     :duration "30m" :cooldown -1 :discarding []}
    {:key "focus-1-1" :title "Focus Work Session 1" :description "Nap" :trigger-text "nap" :trigger-func "(r nap 0)"
     :duration "90m" :cooldown -1 :discarding []}
    {:key "focus-1-2" :title "Focus Work Session 1" :description "14-ish" :trigger-text "14-ish" :trigger-func "(a 14)"
     :duration "90m" :cooldown -1 :discarding []}
    {:key "defocus-1" :title "Defocus Session 1" :description "" :trigger-text "Focus 1" :trigger-func
                                                                                                     "(or
                                                                                                      (r focus-1-2 0)
                                                                                                      (r focus-1-1 0))"
     :duration "30m" :cooldown -1 :discarding []}
    {:key "focus-2" :title "Focus Work Session 2" :description "" :trigger-text "Defocus 1" :trigger-func "(r defocus-1 0)"
     :duration "90m" :cooldown -1 :discarding []}
    {:key "defocus-2" :title "Defocus Session 2" :description "" :trigger-text "Focus 2" :trigger-func "(r focus-2 0)"
     :duration "30m" :cooldown -1 :discarding []}
    {:key "dinner" :title "Dinner" :description "" :trigger-text "18-ish" :trigger-func "(a 18)"
     :duration "40m" :cooldown -1 :discarding []}
    {:key "free" :title "Free Time" :description "" :trigger-text "Dinner" :trigger-func "(r dinner 0)"
     :duration "120m" :cooldown -1 :discarding []}
    {:key "bed-prepare" :title "Prepare for Bed" :description "" :trigger-text "21-ish" :trigger-func "(a 21)"
     :duration "40m" :cooldown -1 :discarding []}
    {:key "sleep" :title "Sleep" :description "" :trigger-text "22-ish/Prepare for Bed" :trigger-func
                                                                                     ;; "(or
                                                                                     ;;  (r bed-prepare 0)
                                                                                     ;;  (a 22))"
                                                                                     "(a 0)"
     :duration "at least 8 hour" :cooldown -1 :discarding []}
    ))
(defn schedulize-routine [routine]
  "routine does not store information on current and discarded, as they only make sense in the day.

    return a list of Events, each have current? and discarded? initialized to nil"
  (map (fn [event] (assoc event :current? nil :discarded? nil :time 0)) routine)
  )
