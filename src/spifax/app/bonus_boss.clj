(ns spifax.app.bonus-boss
  (:require [sugot.lib :as l]
            [sugot.world :as w]
            [spifax.lib])
  (:import [org.bukkit.entity Player EnderDragon]
           [org.bukkit Material]
           [org.bukkit.inventory ItemStack]))

(defn org.bukkit.event.entity.EntityDeathEvent [event]
  (let [living-entity (.getEntity event)
        killer (.getKiller living-entity)]
    (when (and
            (instance? EnderDragon living-entity)
            (instance? Player killer))
      (let [killer-name (.getName killer)
            bow (spifax.lib/create-power6-named-bow killer-name)
            msg (format "[BONUS_BOSS] %s killed an Ender Dragon. It dropped a special bow for %s!"
                        killer-name killer-name)]
        (w/drop-item (.getLocation killer) bow)
        (l/broadcast msg)
        (l/post-lingr msg)))))
