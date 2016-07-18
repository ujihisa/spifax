(ns spifax.app.stack-items
  (:require [sugot.lib :as l]
            [sugot.world :as w])
  (:import [org.bukkit Material Sound]))

; Are `is1` `is2` same except for them?
; * amount
(defn- same-item-stack? [is1 is2]
  (and is1 is2
       (= (.getType is1) (.getType is2))
       (= (.getData is1) (.getData is2))
       (= (.getItemMeta is1) (.getItemMeta is2))))

(defn org.bukkit.event.player.PlayerToggleSneakEvent [event]
  (when (and (.isSneaking event) (.isOnGround (.getPlayer event)))
    (let [player (.getPlayer event)
          item-stack (.getItemInHand player)
          inventory (.getInventory player)
          idx (.getHeldItemSlot inventory)
          next-item-idx (mod (inc idx) 9)
          next-item-stack (.getItem inventory next-item-idx)]
      (when (and
              (contains? #{Material/POTION Material/SPLASH_POTION  Material/LINGERING_POTION}
                        (.getType item-stack))
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
