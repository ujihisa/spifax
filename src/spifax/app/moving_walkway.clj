(ns spifax.app.moving-walkway
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Bukkit Material Effect Sound]
           [org.bukkit.entity Player Minecart]
           [org.bukkit.util Vector]))

(def player-state
  "nil    You can start using walkway if conditions satisfy.
  :moving Walkway is working. You can change pitch/yaw but you can't move
  :idle   Walkway completed. Wait for a while to use again."
  (atom {}))

(defn- is-passable? [loc]
  (let [loc (.clone loc)
        block-above1 (.getBlock (.add loc 0 1 0))
        block-above2 (.getBlock (.add loc 0 1 0))]
    (and
      (= Material/AIR (.getType block-above1))
      (= Material/AIR (.getType block-above2)))))

(def max-distance 20)

(def move-unit 3)

(defn- block-below [loc]
  (.getBlock (.add (.clone loc)
                   0 -1 0)))

(defn- go-next [past-distance player loc [xdiff zdiff :as tuple]]
  (if-let [next-loc (and
                      (< past-distance max-distance)
                      (.isValid player)
                      (.add (.clone loc) xdiff 0 zdiff))]
    (when (is-passable? next-loc)
      (.setPitch next-loc (.getPitch (.getLocation player)))
      (.setYaw next-loc (.getYaw (.getLocation player)))
      (w/play-sound (.getLocation player)
                    Sound/ENTITY_MINECART_RIDING
                    (float 0.2) (float 1.8))
      (.teleport player next-loc)
      (l/later move-unit
        (go-next (inc past-distance) player next-loc tuple)))
    (let [player-name (.getName player)]
      (swap! player-state assoc player-name :idle)
      (l/later (l/sec 5)
        (swap! player-state dissoc player-name)))))

(defn parse-stair
  "Returns nil if `block` is not a stair, or an inverted stair.
  Otherwise returns a tuple of xdiff/zdiff where it's facing."
  [block]
  (when (.endsWith (.name (.getType block))
                   "STAIRS")
    (let [material-data (.getData (.getState block))]
      (when (and
              (not (.isInverted material-data)))
        (let [block-face (.getAscendingDirection material-data)]
          [(.getModX block-face) (.getModZ block-face)])))))

(defn org.bukkit.event.player.PlayerMoveEvent [event]
  (let [player (.getPlayer event)
        player-name (.getName player)
        loc (.getFrom event)
        block-below (block-below loc)]
    (when (and
            (= "ujm" player-name)
            (nil? (get @player-state player-name))
            (.isOnGround player))
      (when-let [tuple (parse-stair block-below)]
        (swap! player-state assoc player-name :moving)
        (go-next 0 player loc tuple)))))
