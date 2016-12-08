(ns spifax.app.driller
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Material Sound]
           [org.bukkit.event.block Action]))

(def driller-max 32)

(defn- driller-shovel? [item]
  (and (= (.getType item) Material/WOOD_SPADE)
       (= (l/get-display-name item) "Driller's Shovel")))

(defn- use-driller-shovel [item]
  (.setDurability item (inc (.getDurability item))))

(defn- break-driller-shovel? [item]
  (< (.getMaxDurability Material/WOOD_SPADE) (.getDurability item)))

(defn- sand-block? [block]
  (= (.getType block) Material/SAND))

(defn- driller [base-block driller-count]
  (when (and (< driller-count driller-max)
             (sand-block? base-block))
    (.breakNaturally base-block)
    (doseq [y [0 -1]
            x [-1 0 1]
            z [-1 0 1]]
      (let [block (.getRelative base-block x y z)]
        (driller block (inc driller-count))))))

(defn org.bukkit.event.player.PlayerInteractEvent* [item action block player play-sound]
  (when (and (driller-shovel? item)
             (= action Action/RIGHT_CLICK_BLOCK))
    (when (sand-block? block)
      (use-driller-shovel item)
      (when (break-driller-shovel? item)
        (l/consume-item player)
        (play-sound Sound/ENTITY_ITEM_BREAK))
      (driller block 0)
      (play-sound Sound/BLOCK_SAND_BREAK))))

(defn org.bukkit.event.player.PlayerInteractEvent [event]
  (#'org.bukkit.event.player.PlayerInteractEvent*
    (.getItemInMainHand (.getInventory (.getPlayer event)))
    (.getAction event)
    (.getClickedBlock event)
    (.getPlayer event)
    (fn [sound]
      (w/play-sound (.getLocation (.getPlayer event)) sound (float 1.0) (float 1.0)))))
