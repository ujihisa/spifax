(ns spifax.app.express-train
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Bukkit Material Effect Sound]
           [org.bukkit.entity Player Minecart]))

(defn rand-around [loc]
  (.add loc (- 0.5 (rand)) (- 0.5 (rand)) (- 0.5 (rand))))

(def max-before-gear-change (atom {}))

; SPEC STORY
;   Player ujm takes a minecart.
;   The cart accelerates to 8m/s either by downhill or powered rails.
;   [Only when this is going to go through very long flat straight rails,
;   this cart accelerates even more, upto 80m/s.]
;
; NOTES
;   This works only with player. Not for cargo carts.
;
; DEVELOPMENT NOTES
;   * Minecart.setMaxSpeed did not seem to be working when you check by
;     Vehicle.getVelocity, but it's actually moving faster. Check by
;     diff of .getTo and .getFrom.
(defn org.bukkit.event.vehicle.VehicleMoveEvent [event]
  (let [vehicle (.getVehicle event)]
    (when-let [passenger (.getPassenger vehicle)]
      (when (and (instance? Minecart vehicle)
                 (instance? Player passenger)
                 (= "ujm" (.getName passenger)))
        (let [player-name (.getName passenger)
              actual-velocity (.subtract (.getTo event) (.getFrom event))]
          (if (< 0.39 (.length actual-velocity))
            (do
              (when (< 0.9 (rand))
                (w/play-sound (.getLocation passenger) Sound/ENTITY_MINECART_RIDING (float 0.5) (float 1.8)))
              (when (< (.getMaxSpeed vehicle) 0.9)
                (swap! max-before-gear-change conj player-name 0)
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
              (swap! max-before-gear-change disj player-name)
              (.setMaxSpeed vehicle 0.4)
              #_(.sendMessage passenger
                            (str "less"
                                 (.lengthSquared (.getVelocity vehicle))
                                 ", "
                                 (.length actual-velocity))))))))))

(when-let [yet (Bukkit/getPlayer "yetdesperate")]
  (dotimes [i (rand-nth [1 20 30 40])]
    (l/later (* 2 i)
      (w/play-effect (rand-around (.getLocation yet))
                     Effect/END_GATEWAY_SPAWN
                     nil))))
