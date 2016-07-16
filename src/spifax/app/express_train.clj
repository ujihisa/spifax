(ns spifax.app.express-train
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Bukkit Material Effect Sound]
           [org.bukkit.entity Player Minecart]))

(defn rand-around [loc]
  (.add loc (- 0.5 (rand)) (- 0.5 (rand)) (- 0.5 (rand))))

(def minecart-max-speed 2.05) ; precisely 2.054000

(defn- go-next [vehicle vehicle-loc passenger velocity]
  (when (and
          (.isValid vehicle)
          (.isValid passenger)
          (= passenger (.getPassenger vehicle)))
    (let [direction (.normalize (.clone velocity))
          next-block (.getBlock
                       (.add (.clone vehicle-loc) direction))]
      (when (= Material/STEP (.getType next-block))
        (l/later 0
          (let [new-loc (.add (.clone vehicle-loc)
                              (.multiply (.clone direction) 1.0))]
            (w/play-sound new-loc Sound/ENTITY_MINECART_RIDING (float 0.2) (float 1.8))
            (doseq [player (Bukkit/getOnlinePlayers)
                    :let [loc (.getLocation next-block)]]
              (l/later (l/sec 0.2)
                (.sendBlockChange player loc Material/FIRE (byte 0)))
              (l/later (l/sec 5)
                (when (.isValid player)
                  (.sendBlockChange player loc (.getType (.getBlock loc)) (.getData (.getBlock loc))))))
            (.setYaw new-loc (.getYaw (.getLocation passenger)))
            (.setPitch new-loc (.getPitch (.getLocation passenger)))
            (let [chunk (.getChunk new-loc)]
              (when-not (.isLoaded chunk)
                (.load chunk)))
            (let [move (fn []
                         (.teleport passenger (.add (.clone new-loc) 0 1 0))
                         (.teleport vehicle new-loc)
                         (.setPassenger vehicle passenger)
                         (.setVelocity vehicle velocity))]
              (move)
              (.setFallDistance passenger 0)
              (.setFallDistance vehicle 0)
              (.addPotionEffect passenger
                                (org.bukkit.potion.PotionEffect.
                                  org.bukkit.potion.PotionEffectType/FIRE_RESISTANCE
                                  20
                                  1))
              (l/later 1
                (move)
                (go-next vehicle new-loc passenger velocity)))))))))

(defn org.bukkit.event.vehicle.VehicleMoveEvent [event]
  (let [vehicle (.getVehicle event)]
    (when-let [passenger (.getPassenger vehicle)]
      (when (and (instance? Minecart vehicle)
                 (instance? Player passenger))
        (let [player-name (.getName passenger)
              velocity (.getVelocity vehicle)]
          (when (< minecart-max-speed (.length velocity))
            (go-next vehicle
                     (.getLocation vehicle)
                     passenger
                     velocity)))))))

#_(try
  (doseq [player (Bukkit/getOnlinePlayers)]
    (w/strike-lightning-effect (.getLocation player))
    (.sendMessage player "You got 1 minecart")
    (w/drop-item (.getLocation player) (org.bukkit.inventory.ItemStack. Material/MINECART 1)))
  #_(when-let [yet (Bukkit/getPlayer "yetdeseparate")]
    (dotimes [i (rand-nth [1 20 30 40])]
      (l/later (* 2 i)
        (w/play-effect (rand-around (.getLocation yet))
                       Effect/END_GATEWAY_SPAWN
                       nil))))
  (catch Exception e e))
