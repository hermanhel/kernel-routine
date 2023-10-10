(ns kernel.ui
  (:require [clojure.string :as s])
  (:require [goog.string :as gstring])
  )

(defn class-concat [x & xs])

(defn time-seconds-to-string [seconds]
  "67 -> 01:07"
  (let [second (mod seconds 60)
        minute (/ (- seconds second) 60)
        ]
    (gstring/format "%02d:%02d" minute second)
     )
  )

(defn Card [key title trigger duration on-click current? time]
  (let [bg (if current? "bg-lime-500" "bg-white dark:bg-gray-800")
        dim (if current? "mb-1 font-normal text-5xl text-gray-700 dark:text-gray-700"  "mb-1 font-normal text-5xl text-gray-700 dark:text-gray-400" )]
  [:div {:onClick (fn [] (on-click key)) :key key :class
         (s/join " " [bg "ml-8 w-11/12 p-6 border border-gray-200 rounded-lg shadow dark:border-gray-700"])}
   [:div {:class "flex justify-between"}
    [:p {:class dim} "→" trigger]
    [:p {:class dim} (when (> time 0)(time-seconds-to-string time))]
    ]

   [:div {:class "flex justify-between"}
    [:h5 {:class "mb-2 text-7xl font-bold tracking-tight text-gray-900 dark:text-white"} title]
    [:p {:class dim} duration]]
   ]
  ))
(defn Bottom-Label []
  [:div {:class "fixed bottom-0 w-full border-t bg-zinc-100 pb-safe dark:border-zinc-800 dark:bg-zinc-900"}
   [:p {:class "h-16 text-zinc-500 text-center text-9xl"}"Kernel"]
   ]
  )

(defn Panel [event-list relative-clock-list on-click]
  [:div {:class "mt-0 p-5 flex-col space-y-5 h-full content-end justify-center bg-white dark:bg-gray-700"}
   (doall
    (for [schedule-event event-list]

      (when (and ((:trigger-func schedule-event) relative-clock-list) (not (:discarded? schedule-event)))
       [Card (:key schedule-event) (:title schedule-event) (:trigger-text schedule-event) (:duration schedule-event) on-click (:current? schedule-event) (:time schedule-event)])
      )
    )
   ;; [Card (:title wake-up)(:trigger wake-up)(:duration wake-up)]
   ;; [Card (:title wake-up)(:trigger wake-up)(:duration wake-up)]
   ;; [Card (:title wake-up)(:trigger wake-up)(:duration wake-up)]
   ]
  )
