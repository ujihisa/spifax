(ns spifax.app.bonus-achievement
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Achievement Material]
           [org.bukkit.inventory ItemStack]))

(defn org.bukkit.event.player.PlayerAchievementAwardedEvent' [event get-player get-achievement get-location]
  (l/post-lingr (format "[ACHIEVEMENT] %s got %s"
                        (get-player event)
                        (get-achievement event)))
  (case (get-achievement event)
    Achievement/OPEN_INVENTORY (let [loc (get-location player)]
                                 (w/strike-lightning-effect loc)
                                 (w/drop-item loc (ItemStack. Material/DIAMOND 64)))
    nil))

(defn org.bukkit.event.player.PlayerAchievementAwardedEvent [event]
  (org.bukkit.event.player.PlayerAchievementAwardedEvent'
    event #(.getPlayer %) #(.getAchievement %) #(.getLocation %)))

