(ns spifax.app.chat
  (:require [sugot.lib :as l]))

(defn org.bukkit.event.player.AsyncPlayerChatEvent' [event get-player get-name get-format get-message]
  (let [player (get-player event)]
    (l/post-lingr-sync (format (get-format event) (get-name player) (get-message event)))))

(defn org.bukkit.event.player.AsyncPlayerChatEvent [event]
  (#'org.bukkit.event.player.AsyncPlayerChatEvent'
    event #(.getPlayer %) #(.getName %) #(.getFormat %) #(.getMessage %)))

(defn org.bukkit.event.player.PlayerLoginEvent [event]
  (l/post-lingr-sync (format "[LOGIN] %s logged in." (.getName (.getPlayer event)))))
