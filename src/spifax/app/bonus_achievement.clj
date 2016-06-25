(ns spifax.app.bonus-achievement
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Achievement Material]
           [org.bukkit.inventory ItemStack]))

(defn org.bukkit.event.player.PlayerAchievementAwardedEvent' [event get-player get-name get-achievement get-location add-level]
  (l/post-lingr (format "[ACHIEVEMENT] %s got %s"
                        (get-name (get-player event))
                        (get-achievement event)))
  (condp = (get-achievement event)
    Achievement/OPEN_INVENTORY (let [loc (get-location (get-player event))]
                                 (w/strike-lightning-effect loc)
                                 (w/drop-item loc (ItemStack. Material/DIAMOND 64)))
    Achievement/MAKE_BREAD (let [loc (get-location (get-player event))]
                             (w/strike-lightning-effect loc)
                             (dotimes [_ 2]
                               (w/drop-item loc (ItemStack. Material/BREAD 64))))
    Achievement/ENCHANTMENTS (let [loc (get-location (get-player event))]
                               (w/strike-lightning-effect loc)
                               (.sendMessage (get-player event) "[ACHIEVEMENT] Level Up Bonus!")
                               (add-level (get-player event) 80))
    nil))

(defn org.bukkit.event.player.PlayerAchievementAwardedEvent [event]
  (#'org.bukkit.event.player.PlayerAchievementAwardedEvent'
    event #(.getPlayer %) #(.getName %) #(.getAchievement %) #(.getLocation %)
    (fn [player n]
      (.setLevel player (+ n (.getLevel player))))))
