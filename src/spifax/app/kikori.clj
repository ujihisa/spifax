(ns spifax.app.kikori
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Material]
           [org.bukkit.event.block Action]))

(defn- kikori-axe? [item]
  (and (= (.getType item) Material/WOOD_AXE)
       (= (l/get-display-name item) "Kikori's Axe")))

(defn- wood-block? [block]
  (contains? #{Material/LOG Material/LOG_2} (.getType block)))

(defn- kikori [base-block]
  (when (wood-block? base-block)
    (.breakNaturally base-block)
    (doseq [y [0 1]
            x [-1 0 1]
            z [-1 0 1]]
      (let [block (.getRelative base-block x y z)]
        (kikori block)))))

(defn org.bukkit.event.player.PlayerInteractEvent* [item action block]
  (when (and (kikori-axe? item)
             (= action Action/RIGHT_CLICK_BLOCK))
    (kikori block)))

(defn org.bukkit.event.player.PlayerInteractEvent [event]
  (#'org.bukkit.event.player.PlayerInteractEvent*
    (.getItemInMainHand (.getInventory (.getPlayer event)))
    (.getAction event)
    (.getClickedBlock event)))
