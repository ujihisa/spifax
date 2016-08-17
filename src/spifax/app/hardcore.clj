(ns spifax.app.hardcore
  (:require [sugot.app.hardcore]))

(defn org.bukkit.event.player.PlayerDropItemEvent [event]
  (sugot.app.hardcore/PlayerDropItemEvent event))

(defn org.bukkit.event.entity.ProjectileHitEvent [event]
  (sugot.app.hardcore/ProjectileHitEvent event))

(defn org.bukkit.event.block.BlockPlaceEvent [event]
  (sugot.app.hardcore/BlockPlaceEvent event))

(defn org.bukkit.event.player.PlayerLoginEvent [event]
  (sugot.app.hardcore/PlayerLoginEvent event))

(defn org.bukkit.event.entity.EntityDamageEvent [event]
  (sugot.app.hardcore/EntityDamageEvent event))

(defn org.bukkit.event.vehicle.VehicleExitEvent [event]
  (sugot.app.hardcore/VehicleExitEvent event))

(defn org.bukkit.event.entity.EntityDeathEvent [event]
  (sugot.app.hardcore/EntityDeathEvent event))

(defn org.bukkit.event.entity.CreatureSpawnEvent [event]
  (sugot.app.hardcore/CreatureSpawnEvent event))

(defn org.bukkit.event.entity.ProjectileLaunchEvent [event]
  (sugot.app.hardcore/ProjectileLaunchEvent event))

(defn org.bukkit.event.player.PlayerInteractAtEntityEvent [event]
  (sugot.app.hardcore/PlayerInteractAtEntityEvent event))

(defn org.bukkit.event.player.PlayerInteractEvent [event]
  (sugot.app.hardcore/PlayerInteractEvent event))

(defn org.bukkit.event.entity.PlayerDeathEvent [event]
  (sugot.app.hardcore/PlayerDeathEvent event))

(defn on-load []
  (sugot.app.hardcore/on-load))
