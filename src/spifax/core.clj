(ns spifax.core
  (:import [org.bukkit.craftbukkit Main]
           [org.bukkit Bukkit]
           [org.bukkit.event Listener])
  (:require [sugot.core]
            [sugot.events]))

(defn- register-all-events [plugin-manager]
  (prn 'register-all-events plugin-manager)
  (doseq [namespace* ['spifax.app.chat 'spifax.app.bonus-achievement
                      'spifax.app.misc 'spifax.app.gold]
          _ [(require namespace*)]
          klass [org.bukkit.event.player.AsyncPlayerChatEvent
                 org.bukkit.event.player.PlayerLoginEvent
                 org.bukkit.event.player.PlayerQuitEvent
                 org.bukkit.event.entity.PlayerDeathEvent
                 org.bukkit.event.player.PlayerAchievementAwardedEvent
                 org.bukkit.event.entity.EntityDeathEvent]]
    (let [sym (symbol (format "%s/%s"
                              (name namespace*)
                              (.getName klass)))]
      (when (ns-resolve namespace* sym)
        (let [safe-f (fn [event]
                       (try
                         ; This lookup has to be dynamic every time
                         ; because this part only happen once at bootup.
                         (ns-resolve namespace* sym)
                         (catch Exception e (.printStackTrace e))))]
          (sugot.core/register-event plugin-manager klass safe-f))))))

(defn- start
  "It's called right after minecraft server is ready"
  [pm]
  (prn :server-ready)
  (register-all-events pm))

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
  (if-let [server (Bukkit/getServer)]
    (-> server (.getPluginManager) (start))
    (prn :server-not-ready))
  (catch Exception e e))
