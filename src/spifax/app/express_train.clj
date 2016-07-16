(ns spifax.app.express-train
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Bukkit Material Effect Sound]
           [org.bukkit.entity Player Minecart]))

(defn rand-around [loc]
  (.add loc (- 0.5 (rand)) (- 0.5 (rand)) (- 0.5 (rand))))

(def minecart-max-speed 2.05) ; precisely 2.054000

(defn-
  ^{:doc "(find-next-loc b f 5) => [5 (f (... (f b)...))]
                                => [2 (f (f b))]
                                => [0 b]"}
  find-next-loc [loc inc-f n-max]
  (let [; If you are going to go to STEP, it goes forward.
        ; If you are already on STEP and next is not wall, it also goes forward.
        continue? (fn [loc next-loc]
                    (or (= Material/STEP (.getType (.getBlock next-loc)))
                        (and (= Material/STEP (.getType (.getBlock loc)))
                             (not (.isOccluding (.getType (.getBlock next-loc)))))))]
    (loop [n 0 loc loc]
      (let [next-loc (inc-f loc)]
        (if (and (< n n-max)
                 (continue? loc next-loc))
          (recur (inc n) next-loc)
          [loc n])))))

(defn- go-next [vehicle vehicle-loc passenger velocity]
  (when (and
          (.isValid vehicle)
          (.isValid passenger)
          (= passenger (.getPassenger vehicle)))
    (let [direction (.normalize (.clone velocity))
          next-loc-f (fn [vehicle-loc direction]
                         (.getBlock
                           (.add (.clone vehicle-loc) direction)))
          [next-loc length] (find-next-loc vehicle-loc
                                           (fn inc-f [loc]
                                             (.add (.clone loc)
                                                   direction))
                                           5)]
      (when (not= length 0)
        (l/later 0
          (w/play-sound next-loc Sound/ENTITY_MINECART_RIDING (float 0.2) (float 1.8))
          (doseq [player (Bukkit/getOnlinePlayers)]
            (l/later (l/sec 0.2)
              (.sendBlockChange player next-loc Material/FIRE (byte 0)))
            (l/later (l/sec 5)
              (when (.isValid player)
                (.sendBlockChange player next-loc (.getType (.getBlock next-loc)) (.getData (.getBlock next-loc))))))
          (.setYaw next-loc (.getYaw (.getLocation passenger)))
          (.setPitch next-loc (.getPitch (.getLocation passenger)))
          (let [move (fn []
                       (.teleport passenger (.add (.clone next-loc) 0 1 0))
                       (.teleport vehicle next-loc)
                       (.setPassenger vehicle passenger)
                       (.setVelocity vehicle velocity))]
            (.setFallDistance passenger 0)
            (.setFallDistance vehicle 0)
            (.addPotionEffect passenger
                              (org.bukkit.potion.PotionEffect.
                                org.bukkit.potion.PotionEffectType/FIRE_RESISTANCE
                                20
                                1))
            (l/later length
              (move)
              (go-next vehicle next-loc passenger velocity))))))))

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
  #_(doseq [player (Bukkit/getOnlinePlayers)]
    (w/strike-lightning-effect (.getLocation player))
    (.sendMessage player "You got 1 minecart")
    (w/drop-item (.getLocation player) (org.bukkit.inventory.ItemStack. Material/MINECART 1)))
  #_(when-let [yet (Bukkit/getPlayer "ujm")]
    (dotimes [i (rand-nth [1 20 30 40])]
      (l/later (* 2 i)
        (w/play-effect (rand-around (.getLocation yet))
                       Effect/END_GATEWAY_SPAWN
                       nil))))
  (catch Exception e e))
