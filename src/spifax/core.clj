(ns spifax.core
  (:import [org.bukkit.craftbukkit Main]
           [org.bukkit Bukkit]
           [org.bukkit.event Listener])
  (:require [sugot.core]
            [sugot.events]))

(defonce already-started (ref false))

(defn- register-all-events [plugin-manager]
  (prn 'register-all-events plugin-manager)
  (doseq [namespace*
          #_['spifax.app.chat]
          ['spifax.app.bonus-achievement
           'spifax.app.bonus-boss
           'spifax.app.chat
           'spifax.app.driller
           'spifax.app.elytra-boost
           'spifax.app.express-train
           'spifax.app.gold
           'spifax.app.hardcore
           'spifax.app.kikori
           'spifax.app.misc
           'spifax.app.moving-walkway
           'spifax.app.nap
           'spifax.app.pull-items
           'spifax.app.single-sleep
           'spifax.app.stack-items]
          _ [(require namespace*)]
          klass [org.bukkit.event.block.BlockBreakEvent
                 org.bukkit.event.block.BlockPlaceEvent
                 org.bukkit.event.entity.CreatureSpawnEvent
                 org.bukkit.event.entity.EntityDamageByEntityEvent
                 org.bukkit.event.entity.EntityDamageEvent
                 org.bukkit.event.entity.EntityDeathEvent
                 org.bukkit.event.entity.PlayerDeathEvent
                 org.bukkit.event.entity.ProjectileHitEvent
                 org.bukkit.event.entity.ProjectileLaunchEvent
                 org.bukkit.event.inventory.InventoryMoveItemEvent
                 org.bukkit.event.player.AsyncPlayerChatEvent
                 org.bukkit.event.player.PlayerAchievementAwardedEvent
                 org.bukkit.event.player.PlayerBedEnterEvent
                 org.bukkit.event.player.PlayerBedLeaveEvent
                 org.bukkit.event.player.PlayerDropItemEvent
                 org.bukkit.event.player.PlayerInteractAtEntityEvent
                 org.bukkit.event.player.PlayerInteractEvent
                 org.bukkit.event.player.PlayerLoginEvent
                 org.bukkit.event.player.PlayerMoveEvent
                 org.bukkit.event.player.PlayerPortalEvent
                 org.bukkit.event.player.PlayerQuitEvent
                 org.bukkit.event.player.PlayerToggleSneakEvent
                 org.bukkit.event.vehicle.VehicleExitEvent
                 org.bukkit.event.vehicle.VehicleMoveEvent
                 org.bukkit.event.weather.LightningStrikeEvent]]
    (let [sym (symbol (format "%s/%s"
                              (name namespace*)
                              (.getName klass)))]
      (when (let [f (ns-resolve namespace* sym)]
              (and f @f (fn? @f)))
        (let [safe-f (fn [event]
                       (try
                         ; This lookup has to be dynamic every time
                         ; because this part only happen once at bootup.
                         (@(ns-resolve namespace* sym) event)
                         (catch Exception e (.printStackTrace e))))]
          (sugot.core/register-event plugin-manager klass safe-f))))))

(defn- start
  "It's called right after minecraft server is ready"
  [pm]
  (prn :server-ready)
  (register-all-events pm)
  ;DIRTY HACK
  (require 'spifax.app.hardcore)
  ((ns-resolve 'spifax.app.hardcore 'on-load)))

(defn init
  "spifax.core/init is the ring server init, registered at project.clj"
  []
  (future
    (Main/main (make-array String 0)))

  ; call `start` once server is ready.
  (loop [server nil]
    (Thread/sleep 100)
    (if server
      (let [pm (-> server .getPluginManager)
            command-map (-> server .getCommandMap)]
        (start pm))
      (recur (try (Bukkit/getServer) (catch Exception e nil))))))

(try
  (when-not @already-started
    (if-let [server (Bukkit/getServer)]
      (do
        (dosync
          (ref-set already-started true))
        (-> server (.getPluginManager) (start)))
      (prn :server-not-ready)))
  (catch Exception e e))
