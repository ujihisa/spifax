(ns spifax.app.bonus-achievement
  (:require [sugot.lib :as l]
            [sugot.world :as w]
            [spifax.lib])
  (:import [org.bukkit Achievement Material]
           [org.bukkit.inventory ItemStack]))

(defn org.bukkit.event.player.PlayerAchievementAwardedEvent' [event get-player get-name get-achievement get-location add-level]
  (l/post-lingr (format "[ACHIEVEMENT] %s got %s"
                        (get-name (get-player event))
                        (get-achievement event)))
  (condp = (get-achievement event)
    Achievement/OPEN_INVENTORY (let [loc (get-location (get-player event))]
                                 (w/strike-lightning-effect loc)
                                 (l/post-lingr "You got 64 diamonds.")
                                 (w/drop-item loc (ItemStack. Material/DIAMOND 64)))
    Achievement/MAKE_BREAD (let [loc (get-location (get-player event))]
                             (w/strike-lightning-effect loc)
                             (dotimes [_ 2]
                               (l/post-lingr "You got 128 breads")
                               (w/drop-item loc (ItemStack. Material/BREAD 64))))
    Achievement/ENCHANTMENTS (let [loc (get-location (get-player event))]
                               (w/strike-lightning-effect loc)
                               (l/post-lingr "You got 80 more levels")
                               (add-level (get-player event) 80))
    Achievement/NETHER_PORTAL (let [loc (get-location (get-player event))]
                                (w/strike-lightning-effect loc)
                                (l/post-lingr "You got 64 glowstones")
                                (w/drop-item loc (ItemStack. Material/GLOWSTONE 64)))
    Achievement/GET_BLAZE_ROD (let [loc (get-location (get-player event))]
                                (w/strike-lightning-effect loc)
                                (l/post-lingr "You got 64 blaze rods")
                                (w/drop-item loc (ItemStack. Material/BLAZE_ROD 64)))
    Achievement/COOK_FISH (let [loc (get-location (get-player event))]
                            (w/strike-lightning-effect loc)
                            (l/post-lingr "You got 64 pufferfishes")
                            (w/drop-item loc (ItemStack. Material/RAW_FISH 64 (short 0) (byte 3))))
    Achievement/END_PORTAL (let [loc (get-location (get-player event))]
                             (w/strike-lightning-effect loc)
                             (l/post-lingr "You got 64 diamonds.")
                             (w/drop-item loc (ItemStack. Material/DIAMOND 64)))
    Achievement/FLY_PIG (let [loc (get-location (get-player event))]
                          (w/strike-lightning-effect loc)
                          (l/post-lingr "You got 80 more levels")
                          (add-level (get-player event) 80))
    Achievement/SNIPE_SKELETON (let [player (get-player event)
                                     loc (get-location player)
                                     player-name (.getName player)
                                     bow (spifax.lib/create-power6-named-bow player-name)
                                     msg (format "[BONUS_ACHIEVEMENT] %s got a special bow."
                                                 player-name)]
                                 (w/strike-lightning-effect loc)
                                 (l/broadcast msg)
                                 (l/post-lingr msg)
                                 (w/drop-item loc bow))
    nil))

(defn org.bukkit.event.player.PlayerAchievementAwardedEvent [event]
  (#'org.bukkit.event.player.PlayerAchievementAwardedEvent'
    event #(.getPlayer %) #(.getName %) #(.getAchievement %) #(.getLocation %)
    (fn [player n]
      (.setLevel player (+ n (.getLevel player))))))
