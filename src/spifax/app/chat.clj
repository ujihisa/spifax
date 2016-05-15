(ns spifax.app.chat
  (:require [spifax.lib :as l]))

(defn org.bukkit.event.player.AsyncPlayerChatEvent [event]
  (let [player (.getPlayer event)]
    (prn 'chat player event)))
