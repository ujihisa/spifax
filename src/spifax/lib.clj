(ns spifax.lib
  (:require [sugot.lib])
  (:import [org.bukkit Bukkit ChatColor Material]
           [org.bukkit.enchantments Enchantment]
           [org.bukkit.inventory ItemStack]))

(defn- set-name [item-stack name*]
  (let [item-meta (.getItemMeta item-stack)]
    (.setDisplayName item-meta name*)
    (.setItemMeta item-stack item-meta)))

(defn create-power6-named-bow [player-name]
  (let [bow (ItemStack. Material/BOW 1)]
    (doto bow
      (.addUnsafeEnchantment Enchantment/ARROW_DAMAGE 6)
      (set-name (format "%sの弓" player-name)))))

(defn inc-durability
  "This does not check if it gets broken. It just increments."
  [item-stack]
  (.setDurability item-stack (inc (.getDurability item-stack))))
