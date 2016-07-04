(ns spifax.app.pull-items
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Bukkit Sound]
           [org.bukkit.entity Item]))

(defn org.bukkit.event.player.PlayerToggleSneakEvent' [player player-loc sneaking? on-ground?]
  (when (and sneaking? (not on-ground?))
    (let [items (for [entity (.getNearbyEntities player 20.0 5.0 20.0)
                      :when (instance? Item entity)]
                  entity)]
      (doseq [[i item] (map-indexed vector items)]
        (l/later (* 10 i)
          (w/play-sound (.getLocation player) Sound/ENTITY_CAT_AMBIENT (float 1.0) (float 1.0))
          (.teleport item player-loc))))))

(defn org.bukkit.event.player.PlayerToggleSneakEvent [event]
  (let [player (.getPlayer event)]
    (#'org.bukkit.event.player.PlayerToggleSneakEvent'
      player
      (.getLocation player)
      (.isSneaking event)
      (.isOnGround player))))
