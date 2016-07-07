(ns spifax.app.express-train
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Bukkit Material Effect Sound]
           [org.bukkit.entity Player Minecart]))

(defn rand-around [loc]
  (.add loc (- 0.5 (rand)) (- 0.5 (rand)) (- 0.5 (rand))))

(def minecart-max-speed 2.05) ; precisely 2.054000

(defn- go-next [vehicle passenger direction]
  (when (and
          (.isValid vehicle)
          (.isValid passenger)
          (= passenger (.getPassenger vehicle)))
    (let [vehicle-loc (.getLocation vehicle)
          next-block (.getBlock
                       (.add (.clone vehicle-loc) direction))]
      (when (= Material/STEP (.getType next-block))
        (.sendMessage passenger (str "good" (.getLocation vehicle) " " (.getLocation next-block)))
        (.setVelocity vehicle (.zero (.getVelocity vehicle)))
        (l/later 0
          (let [new-loc (.add (.add (.clone vehicle-loc) (.multiply (.clone direction)
                                                                    15))
                              0.0 0.5 0.0)]
            (w/strike-lightning-effect new-loc)
            (.setYaw new-loc (.getYaw (.getLocation passenger)))
            (.setPitch new-loc (.getPitch (.getLocation passenger)))
            (.teleport passenger new-loc)
            (.teleport vehicle new-loc)
            (.setPassenger vehicle passenger)
            #_(l/later 1
              (when (< 0 (rand 100))
                (go-next vehicle passenger direction)))))))))

; SPEC STORY
;   Player ujm takes a minecart.
;   The cart accelerates to 8m/s either by downhill or powered rails.
;   [Only when this is going to go through very long flat straight rails,
;   this cart accelerates even more, upto 80m/s.]
;
; NOTES
;   This works only with player. Not for cargo carts.
(defn org.bukkit.event.vehicle.VehicleMoveEvent [event]
  (let [vehicle (.getVehicle event)]
    (when-let [passenger (.getPassenger vehicle)]
      (when (and (instance? Minecart vehicle)
                 (instance? Player passenger)
                 #_(= "ujm" (.getName passenger)))
        (let [player-name (.getName passenger)
              velocity (.getVelocity vehicle)]
          (when (< minecart-max-speed (.length velocity))
            (go-next vehicle
                     passenger
                     (.normalize (.clone velocity)))
            #_(let [next-block (get-next-block velocity (.getLocation vehicle))]
              (when (= Material/STEP (.getType next-block))
                (.setCancelled event)
                (go-next vehicle passenger next-block))))
          #_(if (< 0.39 (.length actual-velocity))
            (do
              (when (< 0.9 (rand))
                (w/play-sound (.getLocation passenger) Sound/ENTITY_MINECART_RIDING (float 0.5) (float 1.8)))
              (when (< (.getMaxSpeed vehicle) 0.9)
                (w/play-effect (.getLocation passenger) Effect/END_GATEWAY_SPAWN nil)
                (.setMaxSpeed vehicle 1.0)
                (.sendMessage passenger
                              (str "more"
                                   (.getMaxSpeed vehicle)
                                   ", "
                                   (.lengthSquared (.getVelocity vehicle))
                                   ", "
                                   (.length actual-velocity)))))
            (do
              (.setMaxSpeed vehicle 0.4)
              #_(.sendMessage passenger
                            (str "less"
                                 (.lengthSquared (.getVelocity vehicle))
                                 ", "
                                 (.length actual-velocity))))))))))

(try
  (when-let [yet (Bukkit/getPlayer "ryunix")]
    (dotimes [i (rand-nth [1 20 30 40])]
      (l/later (* 2 i)
        (w/play-effect (rand-around (.getLocation yet))
                       Effect/END_GATEWAY_SPAWN
                       nil))))
  (catch Exception e (prn e)))
