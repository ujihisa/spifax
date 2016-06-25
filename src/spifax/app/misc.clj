(ns spifax.app.misc
  (:require [sugot.lib :as l]))

(defn org.bukkit.event.player.PlayerQuitEvent [event]
  (l/post-lingr (.getQuitMessage event)))

(defn org.bukkit.event.entity.PlayerDeathEvent [event]
  (l/post-lingr (.getDeathMessage event)))
