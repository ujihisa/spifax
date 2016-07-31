(ns spifax.app.moving-walkway
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Bukkit Material Effect Sound]
           [org.bukkit.entity Player Minecart]
           [org.bukkit.util Vector]))

(defonce player-state
  ^{:doc "nil    You can start using walkway if conditions satisfy.
  :moving Walkway is working. You can change pitch/yaw but you can't move
  :idle   Walkway completed. Wait for a while to use again."}
  (atom {}))

(defn- is-passable? [loc]
  (let [loc (.clone loc)
        block-above1 (.getBlock (.add loc 0 1 0))
        block-above2 (.getBlock (.add loc 0 1 0))]
    (and
      (= Material/AIR (.getType block-above1))
      (= Material/AIR (.getType block-above2)))))

(def max-distance 30)

(defn- block-below [loc]
  (.getBlock (.add (.clone loc)
                   0 -1 0)))

(defn- is-stair? [block]
  (.endsWith (.name (.getType block))
             "STAIRS"))

(defn- parse-stair
  "Returns nil if `block` is not a stair, or an inverted stair.
  Otherwise returns a tuple of xdiff/zdiff where it's facing."
  [block]
  (when (is-stair? block)
    (let [material-data (.getData (.getState block))]
      (when (and
              (not (.isInverted material-data)))
        (let [block-face (.getAscendingDirection material-data)]
          [(.getModX block-face) (.getModZ block-face)])))))

(defn- go-next
  "This chains itself, as long as some conditions are satisfied."
  [past-distance player loc [xdiff zdiff :as tuple]]
  (let [next-loc (delay
                   (.add (.clone loc) xdiff 0 zdiff))
        block-below (delay (block-below loc))
        continue? (and
                    (< past-distance max-distance)
                    (.isValid player)
                    (is-stair? @block-below)
                    (= tuple (parse-stair @block-below))
                    (is-passable? @next-loc))]
    (if continue?
      (do
        (.setPitch @next-loc (.getPitch (.getLocation player)))
        (.setYaw @next-loc (.getYaw (.getLocation player)))
        (w/play-sound (.getLocation player)
                      Sound/ENTITY_MINECART_RIDING
                      (float 0.2) (float 1.8))
        (.teleport player @next-loc)
        (l/later 2
          (go-next (inc past-distance) player @next-loc tuple)))
      (let [player-name (.getName player)]
        (swap! player-state assoc player-name :idle)
        (l/later (l/sec 5)
          (swap! player-state dissoc player-name))))))

(defn- move-to-the-centre [player stair before-loc set-after-loc]
  (let [loc (.add (.getLocation stair)
                  0.5
                  1.0
                  0.5)]
    (.setPitch loc (.getPitch before-loc))
    (.setYaw loc (.getYaw before-loc))
    (set-after-loc loc)
    loc))

(defn org.bukkit.event.player.PlayerMoveEvent [event]
  (let [player (.getPlayer event)
        player-name (.getName player)
        loc (.getTo event)
        block-below (block-below loc)]
    (when (and
            (nil? (get @player-state player-name))
            (.isOnGround player))
      (when-let [[xdiff zdiff :as tuple] (parse-stair block-below)]
        ; Only at the first time you confirm that the stairs continues
        ; at least one more block
        (when (let [next-block (.getBlock (.add (.getLocation block-below)
                                                xdiff 0 zdiff))]
                (and (is-stair? next-block)
                     (= tuple (parse-stair next-block))))
          ; If it looks good, adjust to the centre.
          (let [centre-loc (move-to-the-centre player block-below loc #(.setTo event %))]
            (swap! player-state assoc player-name :moving)
            (go-next 0 player centre-loc tuple)))))))
