(ns spifax.app.stack-items
  (:require [sugot.lib :as l]))

(defn org.bukkit.event.player.PlayerToggleSneakEvent [event]
  #_(when (.isSneaking event)
    (let [player (.getPlayer event)
          inventory (.getInventory player)
          item-stacks (.getExtraContents inventory)]
      (prn (into [] item-stacks)))))
