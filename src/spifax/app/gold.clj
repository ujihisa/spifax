(ns spifax.app.gold
  (:import [org.bukkit.entity Monster]
           [org.bukkit Material])
  (:require [sugot.lib :as l]
            [sugot.world :as w]))

(defn org.bukkit.event.entity.EntityDeathEvent' [entity drops]
  (condp instance? entity
    Monster
    (let [equipment (.getEquipment entity)]
      (doseq [armour (into [] (.getArmorContents equipment))
              :when (.startsWith (.toString (.getType armour)) "GOLD")]
        (prn :cool armour)))
    nil))

(defn org.bukkit.event.entity.EntityDeathEvent [event]
  (#'org.bukkit.event.entity.EntityDeathEvent'
    (.getEntity event) (.getDrops event)))
