(ns spifax.app.pull-items
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Bukkit Sound]
           [org.bukkit.entity Item ExperienceOrb]))

(defn org.bukkit.event.player.PlayerToggleSneakEvent'
  [player player-loc sneaking? on-ground? get-entities-around teleport]
  (when (and sneaking? (not on-ground?))
    (let [items (for [entity (get-entities-around player)
                      :when (or (instance? Item entity)
                                (instance? ExperienceOrb entity))]
                  entity)]
      (doseq [[i item] (map-indexed vector items)]
        (l/later (* 10 i)
          (w/play-sound player-loc Sound/ENTITY_CAT_AMBIENT (float 0.2) (float 1.0))
          (teleport item player-loc))))))

(defn org.bukkit.event.player.PlayerToggleSneakEvent [event]
  (let [player (.getPlayer event)]
    (#'org.bukkit.event.player.PlayerToggleSneakEvent'
      player
      (.getLocation player)
      (.isSneaking event)
      (.isOnGround player)
      (fn get-entities-around [player]
        (.getNearbyEntities player 20.0 10.0 20.0))
      (fn teleport [entity loc]
        (.teleport entity loc)))))
