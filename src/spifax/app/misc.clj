(ns spifax.app.misc
  (:require [sugot.lib :as l]))

(defn org.bukkit.event.player.PlayerQuitEvent [event]
  (l/post-lingr (.getQuitMessage event)))

(defn org.bukkit.event.entity.PlayerDeathEvent [event]
  (l/post-lingr (.getDeathMessage event)))

(defn org.bukkit.event.player.PlayerPortalEvent [event]
  (let [new-loc (.getTo event)
        message (format (rand-nth ["[MISC] %s entered %s via a portal (%d, %d)\n"
                                   "[MISC] %sさんがポータルで%sに行った模様 (%d, %d)\n"
                                   "[MISC] %sさんがポータルで%sに行けていたらどれだけよかったか... (%d, %d)\n"
                                   "[MISC] %sさんがポポポポポポポターァルで%sに突入 (%d, %d)\n"])
                        (.getName (.getPlayer event))
                        (.getName (.getWorld new-loc))
                        (.getBlockX new-loc)
                        (.getBlockZ new-loc))]
    (l/broadcast message)
    (l/post-lingr message)))

(defn org.bukkit.event.weather.LightningStrikeEvent [event]
  (when-not (.isEffect (.getLightning event))
    (let [loc (.getLightning event)
          message (format "[MISC] (%d, %d, %d) に落雷しました。"
                          (.getX loc)
                          (.getY loc)
                          (.getZ loc))]
      (l/broadcast message)
      (l/post-lingr message))))
