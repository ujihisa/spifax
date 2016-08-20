(ns spifax.app.express-train
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Bukkit Material Effect Sound]
           [org.bukkit.entity Player Minecart]
           [org.bukkit.util Vector]))

(defn rand-around [loc]
  (.add loc (- 0.5 (rand)) (- 0.5 (rand)) (- 0.5 (rand))))

(def minecart-max-speed 2.05) ; precisely 2.054000

(def minecart-default-flying-velocity-mod (Vector. 0.95 0.95 0.95))

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

(defn- can-go-next? [vehicle passenger]
  (and
    (.isValid vehicle)
    (.isValid passenger)
    (= passenger (.getPassenger vehicle))))

(defn- go-next [vehicle vehicle-loc passenger velocity]
  (let [direction (.normalize (.clone velocity))
        next-loc-f (fn [vehicle-loc direction]
                     (.getBlock
                       (.add (.clone vehicle-loc) direction)))
        [next-loc length] (find-next-loc vehicle-loc
                                         (fn inc-f [loc]
                                           (.add (.clone loc)
                                                 direction))
                                         20)]
    (when (not= length 0)
      (.setFlyingVelocityMod vehicle (Vector.))
      (w/play-sound next-loc Sound/ENTITY_MINECART_RIDING (float 0.2) (float 1.8))
      (l/later 0
          (.setFallDistance passenger 0)
          (.setFallDistance vehicle 0)
          (.addPotionEffect passenger
                            (org.bukkit.potion.PotionEffect.
                              org.bukkit.potion.PotionEffectType/FIRE_RESISTANCE
                              20
                              1))
        (let [move (fn []
                     (doseq [player (Bukkit/getOnlinePlayers)]
                       (.sendBlockChange player next-loc Material/FIRE (byte 0))
                       (l/later (l/sec 5)
                         (when (.isValid player)
                           (.sendBlockChange player next-loc (.getType (.getBlock next-loc)) (.getData (.getBlock next-loc))))))
                     (.setYaw next-loc (.getYaw (.getLocation passenger)))
                     (.setPitch next-loc (.getPitch (.getLocation passenger)))
                     (let [next-loc-above (.add (.clone next-loc) 0 1 0)]
                       (.teleport passenger next-loc-above)
                       (.teleport vehicle next-loc-above))
                     (.setPassenger vehicle passenger)
                     (.setVelocity vehicle velocity))]
          (.setFlyingVelocityMod vehicle minecart-default-flying-velocity-mod)
          (when (can-go-next? vehicle passenger)
            (move)
            (l/later length
              (go-next vehicle next-loc passenger velocity))))))))

(defn org.bukkit.event.vehicle.VehicleMoveEvent [event]
  (let [vehicle (.getVehicle event)]
    (when-let [passenger (.getPassenger vehicle)]
      (when (and (instance? Minecart vehicle)
                 (instance? Player passenger))
        (let [player-name (.getName passenger)
              velocity (.getVelocity vehicle)]
          (when (and (< minecart-max-speed (.length velocity))
                     (can-go-next? vehicle passenger))
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
