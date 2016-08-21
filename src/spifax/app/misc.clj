(ns spifax.app.misc
  (:require [sugot.lib :as l]
            [sugot.world :as w]
            [spifax.app.hardcore])
  (:import [org.bukkit.entity Player Minecart]
           [org.bukkit Bukkit Material]))

(defn org.bukkit.event.entity.PlayerDeathEvent [event]
  (l/post-lingr (.getDeathMessage event)))

(defn org.bukkit.event.player.PlayerPortalEvent [event]
  (let [new-loc (.getTo event)
        message (format (rand-nth ["[MISC] %s entered %s via a portal (%d, %d)"
                                   "[MISC] %sさんがポータルで%sに行った模様 (%d, %d)"
                                   "[MISC] %sさんがポータルで%sに行けていたらどれだけよかったか... (%d, %d)"
                                   "[MISC] %sさんがポポポポポポポターァルで%sに突入 (%d, %d)"])
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
                            (.getData (.getBlock loc)))))))
  ; spawn 4 at the same time, only in the wholeworld
  (when (and
          (= "world" (.getName (.getWorld (.getLocation (.getEntity event)))))
          (= org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason/NATURAL (.getSpawnReason event)))
    (dotimes [_ 3]
      (let [entity (.getEntity event)
            loc (.add (.getLocation entity)
                      (* 0.1 (- (rand) 0.5))
                      0
                      (* 0.1 (- (rand) 0.5)))]
        (let [new-entity (w/spawn loc (class entity))]
          "do nothing for now")))))

; Set of player names, not to update too often
(defonce speedometer (atom #{}))

(defn org.bukkit.event.player.PlayerToggleSneakEvent [event]
  (let [player (.getPlayer event)
        item-stack (delay (.getItemInHand player))]
    (when (and
            (.isSneaking event)
            (not (.isOnGround player))
            @item-stack
            (@#'sugot.app.hardcore/magic-compass? @item-stack)
            (not (sugot.app.hardcore/loc-in-hardcore? (.getLocation player))))
      (let [pname (.getName player)]
        (when-not (@speedometer pname)
          (let [before-loc (.getLocation player)]
            (swap! speedometer assoc pname)
            (l/later (l/sec 1)
              (when (.isValid player)
                (let [after-loc (.getLocation player)]
                  (when (= (.getWorld before-loc) (.getWorld after-loc))
                    (let [new-name (format "%.2f m/s" (.distance before-loc after-loc))]
                      (swap! speedometer dissoc pname)
                      (@#'l/set-name @item-stack new-name))))))))))))
