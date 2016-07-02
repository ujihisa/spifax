(ns spifax.app.single-sleep
  (:require [sugot.lib :as l]
            [sugot.world :as w]))

(def threshold-night 12541)
(def threshold-morning 23459)
(def players-sleeping (atom #{}))

(defn org.bukkit.event.player.PlayerBedEnterEvent* [player-name world get-time set-time]
  (swap! players-sleeping conj player-name)
  (l/later (l/sec 5.0)
    (when (and (@players-sleeping player-name)
               (< threshold-night (get-time world) threshold-morning))
      (swap! players-sleeping disj player-name)
      (let [msg (format "[SINGLE_SLEEP] Good morning, %s"
                        player-name)]
        (l/post-lingr msg)
        (l/broadcast msg))
      (set-time world 0))))

(defn org.bukkit.event.player.PlayerBedEnterEvent [event]
  (#'org.bukkit.event.player.PlayerBedEnterEvent*
    (.getName (.getPlayer event))
    (.getWorld (.getPlayer event))
    (fn [world]
      (.getTime world))
    (fn [world time*]
      (.setTime world time*))))

(defn org.bukkit.event.player.PlayerBedLeaveEvent [event]
  (swap! players-sleeping disj (.getName (.getPlayer event))))
