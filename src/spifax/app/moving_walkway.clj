(ns spifax.app.moving-walkway
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Bukkit Material Effect Sound]
           [org.bukkit.entity Player Minecart]
           [org.bukkit.util Vector]))

(def player-names-moving (atom #{}))

(defn- is-passable-stair? [block]
  (let [loc (.clone (.getLocation block))
        block-above1 (.getBlock loc)
        block-above2 (.getBlock (.add loc 0 1 0))]
    (prn (.getType block-above1) (.getType block-above2))
    (and
      (.endsWith (.name (.getType block)) "STAIRS")
      (= Material/AIR (.getType block-above1))
      (= Material/AIR (.getType block-above2)))))

(defn org.bukkit.event.player.PlayerMoveEvent [event]
  (let [player (.getPlayer event)
        player-name (.getName player)
        loc (.getFrom event)
        block-below (.getBlock (.add (.clone loc)
                                     0 -1 0))]
    (when (and
            (not (@player-names-moving player-name))
            (is-passable-stair? block-below))
      (swap! player-names-moving conj player-name)
      (when (= "ujm" player-name)
        (.sendMessage player "OK")))))
