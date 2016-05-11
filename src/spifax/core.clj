(ns spifax.core
  (:import [org.bukkit.craftbukkit Main]
           [org.bukkit Bukkit]
           [org.bukkit.event Listener])
  (:require [spifax.events]))

(defn- register-all-events [plugin-manager]
  (prn 'register-all-events plugin-manager))

(defn- start
  "It's called right after minecraft server is ready"
  []
  (prn :server-ready)
  (let [pm (-> server .getPluginManager)]
    (register-all-events pm)))

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
        (start))
      (recur (try (Bukkit/getServer) (catch Exception e nil))))))
