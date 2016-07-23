(ns spifax.app.stack-items
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Material Sound]
           [org.bukkit.block Hopper Chest DoubleChest]))

; Are `is1` `is2` same except for them?
; * amount
(defn- same-item-stack? [is1 is2]
  (and is1 is2
       (= (.getType is1) (.getType is2))
       (= (.getData is1) (.getData is2))
       (= (.getItemMeta is1) (.getItemMeta is2))))

(defn is-potion? [item-stack]
  (contains? #{Material/POTION Material/SPLASH_POTION  Material/LINGERING_POTION}
             (.getType item-stack)))

(defn org.bukkit.event.player.PlayerToggleSneakEvent [event]
  (when (and (.isSneaking event) (.isOnGround (.getPlayer event)))
    (let [player (.getPlayer event)
          item-stack (.getItemInHand player)
          inventory (.getInventory player)
          idx (.getHeldItemSlot inventory)
          next-item-idx (mod (inc idx) 9)
          next-item-stack (.getItem inventory next-item-idx)]
      (when (and
              (is-potion? item-stack)
              (same-item-stack? item-stack next-item-stack))
        (let [new-amount (+ (.getAmount item-stack) (.getAmount next-item-stack))
              [new-amount carryover] [(min new-amount 64) (max 0 (- new-amount 64))]]
          (.sendMessage player (format "[STACK_ITEMS] %sをスタックしました。 (%d + %d = %d + %d)"
                                       (.getType item-stack)
                                       (.getAmount item-stack)
                                       (.getAmount next-item-stack)
                                       new-amount
                                       carryover))
          (.setAmount item-stack new-amount)
          (if (= 0 carryover)
            (.setItem inventory next-item-idx nil)
            (.setAmount next-item-stack carryover))
          (w/play-sound (.getLocation player)
                        Sound/BLOCK_BREWING_STAND_BREW
                        (float 1.0)
                        (float 2.0)))))))

(defn- get-last-item [inventory]
  (try
    (.getItem inventory (dec (.firstEmpty inventory)))
    (catch Exception e nil)))

(defn org.bukkit.event.inventory.InventoryMoveItemEvent [event]
  (let [destination (.getDestination event)
        source (.getSource event)
        item-stack (.getItem event)]
    (when (and
            (not (.isCancelled event))
            (is-potion? item-stack)
            (instance? Hopper (.getHolder source))
            (or (instance? Chest (.getHolder destination))
                (instance? DoubleChest (.getHolder destination))))
      (let [last-item (get-last-item destination)]
        (when (and
                last-item
                (is-potion? last-item)
                (< (.getAmount last-item) 64))
          (.setCancelled event true)
          (.setAmount last-item (inc (.getAmount last-item)))
          #_(l/broadcast (prn-str :destination (.getHolder destination) :source (.getHolder source) :item item-stack
                                :last (get-last-item destination))))))))
