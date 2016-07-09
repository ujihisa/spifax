(ns spifax.app.misc
  (:require [sugot.lib :as l])
  (:import [org.bukkit.entity Player Minecart]))

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
    (let [loc (.getLocation (.getLightning event))
          message (format "[MISC] (%d, %d, %d) に落雷しました。"
                          (int (.getX loc))
                          (int (.getY loc))
                          (int (.getZ loc)))]
      (l/broadcast message)
      (l/post-lingr message))))

(defn org.bukkit.event.entity.EntityDamageByEntityEvent' [entity damager]
  #_(when (and entity
             (instance? org.bukkit.entity.Player entity)
             (= "ujm" (.getName entity))
             damager)
    (.sendMessage entity (format "[MISC] %s damaged %s (%s)."
                                 damager
                                 entity
                                 (.isBlocking entity)))))

(defn org.bukkit.event.entity.EntityDamageByEntityEvent [event]
  (#'org.bukkit.event.entity.EntityDamageByEntityEvent'
    (.getEntity event)
    (.getDamager event)))

(defn org.bukkit.event.entity.EntityDamageEvent [event]
  #_(let [entity (.getEntity event)]
    (when (and (instance? Player entity)
               (.getVehicle entity)
               (instance? Minecart (.getVehicle entity)))
      (.sendMessage entity
                    (format "[MISC] %s" [(.getCause event)])))))
