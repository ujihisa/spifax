(ns spifax.app.nap
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Material]
           [org.bukkit.event.block Action]))

(def threshold-night 12541)

(defn org.bukkit.event.player.PlayerInteractEvent*
  [event block action now player get-location get-name]
  (when (and (= block Material/BED_BLOCK)
             (= action Action/RIGHT_CLICK_BLOCK)
             (< now threshold-night))
    (let [msg (format "[NAP] %sさんがお昼寝しました" (get-name player))]
      (l/broadcast msg))
    (.setBedSpawnLocation player (get-location player) true)
    (.teleport player (.getBedSpawnLocation player))
    (l/set-cancelled event)))

(defn org.bukkit.event.player.PlayerInteractEvent [event]
  (#'org.bukkit.event.player.PlayerInteractEvent*
    event
    (some-> event .getClickedBlock .getType)
    (.getAction event)
    (.getTime (.getWorld (.getPlayer event)))
    (.getPlayer event)
    (fn [player]
      (.getLocation player))
    (fn [player]
      (.getName player))))
