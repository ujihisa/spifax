(ns spifax.app.kikori
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Material Sound]
           [org.bukkit.event.block Action]))

(def kikori-max 128)

(defn- kikori-axe? [item]
  (and (= (.getType item) Material/WOOD_AXE)
       (= (l/get-display-name item) "Kikori's Axe")))

(defn- use-kikori-axe [item]
  (.setDurability item (inc (.getDurability item))))

(defn- break-kikori-axe? [item]
  (< (.getMaxDurability Material/WOOD_AXE) (.getDurability item)))

(defn- wood-block? [block]
  (contains? #{Material/LOG Material/LOG_2} (.getType block)))

(defn- kikori [base-block kikori-count]
  (when (and (< kikori-count kikori-max)
             (wood-block? base-block))
    (.breakNaturally base-block)
    (doseq [y [0 1]
            x [-1 0 1]
            z [-1 0 1]]
      (let [block (.getRelative base-block x y z)]
        (kikori block (inc kikori-count))))))

(defn org.bukkit.event.player.PlayerInteractEvent* [item action block player play-sound]
  (when (and (kikori-axe? item)
             (= action Action/RIGHT_CLICK_BLOCK))
    (when (wood-block? block)
      (use-kikori-axe item)
      (when (break-kikori-axe? item)
        (l/consume-item player)
        (play-sound Sound/ENTITY_ITEM_BREAK))
      (kikori block 0)
      (play-sound Sound/BLOCK_WOOD_BREAK))))

(defn org.bukkit.event.player.PlayerInteractEvent [event]
  (#'org.bukkit.event.player.PlayerInteractEvent*
    (.getItemInMainHand (.getInventory (.getPlayer event)))
    (.getAction event)
    (.getClickedBlock event)
    (.getPlayer event)
    (fn [sound]
      (w/play-sound (.getLocation (.getPlayer event)) sound (float 1.0) (float 1.0)))))
