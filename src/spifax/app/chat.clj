(ns spifax.app.chat
  (:require [sugot.lib :as l]))

(defn org.bukkit.event.player.AsyncPlayerChatEvent' [event get-player get-name get-format get-message]
  (let [player (get-player event)]
    (l/post-lingr (format (get-format event) (get-name player) (get-message event)))))

(defn org.bukkit.event.player.AsyncPlayerChatEvent [event]
  (#'org.bukkit.event.player.AsyncPlayerChatEvent'
    event #(.getPlayer %) #(.getName %) #(.getFormat %) #(.getMessage %)))

(defn org.bukkit.event.player.PlayerLoginEvent [event]
  (l/later 0
    (let [player (.getPlayer event)
          loc (.getLocation player)]
      (l/post-lingr (format "[LOGIN] %s logged in at (%d, %d, %d)."
                            (.getName player)
                            (int (.getX loc))
                            (int (.getY loc))
                            (int (.getZ loc)))))))

(defn org.bukkit.event.player.PlayerQuitEvent [event]
  (let [player (.getPlayer event)
        loc (.getLocation player)]
    (l/post-lingr (format "[LOGOUT] %s logged out at (%d, %d, %d)."
                          (.getName player)
                          (int (.getX loc))
                          (int (.getY loc))
                          (int (.getZ loc))))))
