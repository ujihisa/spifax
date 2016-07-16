(ns spifax.app.misc
  (:require [sugot.lib :as l])
  (:import [org.bukkit.entity Player Minecart]
           [org.bukkit Bukkit Material]))

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
  (let [entity (.getEntity event)]
    (when (and (instance? Player entity)
               (.getVehicle entity)
               (instance? Minecart (.getVehicle entity)))
      (.sendMessage entity
                    (format "[MISC] %s" [(.getCause event)])))))

(defn org.bukkit.event.entity.CreatureSpawnEvent [event]
  (when (or (= org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason/NATURAL (.getSpawnReason event))
            (= org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason/JOCKEY (.getSpawnReason event))
            (= org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason/MOUNT (.getSpawnReason event))
            (= org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason/REINFORCEMENTS (.getSpawnReason event))
            (= org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason/VILLAGE_INVASION (.getSpawnReason event)))
    (let [loc (.add (.getLocation (.getEntity event)) 0 -1 0)]
      (doseq [player (Bukkit/getOnlinePlayers)]
        (if (.isLiquid (.getBlock loc))
          (.sendBlockChange player loc Material/GLOWSTONE (byte 0))
          (.sendBlockChange player loc Material/NETHER_WART_BLOCK (byte 0))))
      (l/later (l/sec 300)
        (doseq [player (Bukkit/getOnlinePlayers)]
          (.sendBlockChange player
                            loc
                            (.getType (.getBlock loc))
                            (.getData (.getBlock loc))))))))
