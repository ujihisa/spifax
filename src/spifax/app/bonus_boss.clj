(ns spifax.app.bonus-boss
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit.entity Player EnderDragon]
           [org.bukkit Material]
           [org.bukkit.inventory ItemStack]))

(defn org.bukkit.event.entity.EntityDeathEvent [event]
  (let [living-entity (.getEntity event)
        killer (.getKiller living-entity)]
    (when (and
            (instance? EnderDragon living-entity)
            (instance? Player killer))
      (let [killer-name (.getName killer)]
        nil))))
