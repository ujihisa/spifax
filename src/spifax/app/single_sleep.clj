(ns spifax.app.single-sleep
  (:require [sugot.lib :as l]
            [sugot.world :as w]))

(def threshold-night 12541)
(def threshold-morning 23459)

(defn org.bukkit.event.player.PlayerBedEnterEvent* [player world get-time set-time]
  (l/later (l/sec 5.0)
    (when (< threshold-night (get-time world) threshold-morning)
      (l/post-lingr "[SINGLE_SLEEP] Good morning")
      (l/broadcast "[SINGLE_SLEEP] Good morning")
      (set-time world 0))))

(defn org.bukkit.event.player.PlayerBedEnterEvent [event]
  (#'org.bukkit.event.player.PlayerBedEnterEvent*
    (.getPlayer event)
    (.getWorld (.getPlayer event))
    (fn [world]
      (.getTime world))
    (fn [world time*]
      (.setTime world time*))))
