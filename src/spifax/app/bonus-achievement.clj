(ns spifax.app.bonus-achievement
  (:require [sugot.lib :as l]))

(defn org.bukkit.event.player.PlayerAchievementAwardedEvent [event get-player get-achievement]
  (l/post-lingr (format "[ACHIEVEMENT] %s got %s"
                        (get-player event)
                        (get-achievement event))))

(defn org.bukkit.event.player.PlayerAchievementAwardedEvent [event]
  (org.bukkit.event.player.PlayerAchievementAwardedEvent'
    event #(.getPlayer %) #(.getAchievement %)))

