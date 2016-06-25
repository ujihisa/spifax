(ns spifax.core
  (:import [org.bukkit.craftbukkit Main]
           [org.bukkit Bukkit]
           [org.bukkit.event Listener])
  (:require [sugot.core]
            [sugot.events]))

(defn- register-all-events [plugin-manager]
  (prn 'register-all-events plugin-manager)
  (let [namespace* 'spifax.app.chat
        klass org.bukkit.event.player.AsyncPlayerChatEvent
        sym (symbol (format "%s/%s"
                            (name namespace*)
                            (.getName klass)))
        _ (require namespace*)
        f (ns-resolve namespace* sym)]
    (sugot.core/register-event plugin-manager klass f))
  (let [namespace* 'spifax.app.chat
        klass org.bukkit.event.player.PlayerLoginEvent
        sym (symbol (format "%s/%s"
                            "spifax.app.chat"
                            (.getName klass)))
        _ (require namespace*)
        f (ns-resolve namespace* sym)]
    (sugot.core/register-event plugin-manager klass f))
  (let [namespace* 'spifax.app.bonus-achievement
        klass org.bukkit.event.player.PlayerAchievementAwardedEvent
        sym (symbol (format "%s/%s"
                            "spifax.app.bonus-achievement"
                            (.getName klass)))
        _ (require namespace*)
        f (ns-resolve namespace* sym)]
    (sugot.core/register-event plugin-manager klass f)))

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
