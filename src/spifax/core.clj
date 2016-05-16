(ns spifax.core
  (:import [org.bukkit.craftbukkit Main]
           [org.bukkit Bukkit]
           [org.bukkit.event Listener])
  (:require [sugot.core]
            [sugot.events]))

; Refactor this part -- dead copy from sugot
(defn register-event [pm event-type f]
  (let [listener (reify Listener)
        executor (reify org.bukkit.plugin.EventExecutor
                   (execute [this listener event]
                     (when (instance? event-type event)
                       (try
                         (f event)
                         (catch Exception e (.printStackTrace e))))))
        priority org.bukkit.event.EventPriority/NORMAL]
    (-> pm (.registerEvent event-type listener priority executor sugot.core/dummy-sugot-plugin))))
; End

(defn- register-all-events [plugin-manager]
  (prn 'register-all-events plugin-manager)
  (require 'spifax.app.chat)
  (let [klass org.bukkit.event.player.AsyncPlayerChatEvent
        f (read-string "spifax.app.chat/org.bukkit.event.player.AsyncPlayerChatEvent")]
    (register-event plugin-manager klass f)))

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
