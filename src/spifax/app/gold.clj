(ns spifax.app.gold
  (:import [org.bukkit.entity Monster]
           [org.bukkit Material]
           [org.bukkit.inventory ItemStack])
  (:require [sugot.lib :as l]
            [sugot.world :as w]))

(defn org.bukkit.event.entity.EntityDeathEvent' [entity drops get-location]
  (condp instance? entity
    Monster
    (let [equipment (.getEquipment entity)]
      (doseq [armour (into [] (.getArmorContents equipment))
              :when (.startsWith (.toString (.getType armour)) "GOLD")]
        (l/post-lingr (format "[GOLD] %s dropped a gold."
                              (class entity)))
        (w/drop-item (get-location entity) (ItemStack. Material/GOLD_INGOT 1))))
    nil))

(defn org.bukkit.event.entity.EntityDeathEvent [event]
  (#'org.bukkit.event.entity.EntityDeathEvent'
    (.getEntity event) (.getDrops event)
    #(fn [entity] (.getLocation entity))))
