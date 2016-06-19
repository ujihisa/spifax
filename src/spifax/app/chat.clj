(ns spifax.app.chat
  (:require [sugot.lib :as l]))

(defn org.bukkit.event.player.AsyncPlayerChatEvent' [event get-player get-name get-format]
  (let [player (get-player event)]
    (prn 'chat player event)
    (l/post-lingr-sync (format (get-format event) (get-name player) message))))

(defn org.bukkit.event.player.AsyncPlayerChatEvent [event]
  (org.bukkit.event.player.AsyncPlayerChatEvent'
    event #(.getPlayer %) #(.getName %) #(.getFormat %)))

(defn org.bukkit.event.player.PlayerLoginEvent [event]
  (prn 'org.bukkit.event.player.PlayerLoginEvent)
  (l/post-lingr-sync (format "[LOGIN] %s logged in." (.getName (.getPlayer event)))))
