(ns spifax.core
  (:import [org.bukkit.craftbukkit Main]
           [org.bukkit Bukkit]
           [org.bukkit.event Listener])
  (:require [spifax.events]))

; Refactor this part -- dead copy from sugot
(def dummy-sugot-plugin
  (reify org.bukkit.plugin.Plugin
    (getConfig [this] nil)
    (getDatabase [this] nil)
    (getDataFolder [this] nil)
    (getDefaultWorldGenerator [this worldName id] nil)
    (getDescription [this] nil)
    (getLogger [this] nil)
    (getName [this] "sugot")
    (getPluginLoader [this] nil)
    (getResource [this filename] nil)
    (getServer [this] (Bukkit/getServer))
    (isEnabled [this] true)
    (isNaggable [this] nil)
    (onDisable [this] nil)
    (onEnable [this] (prn 'onEnable this))
    (onLoad [this] (prn 'onLoad this))
    (reloadConfig [this] nil)
    (saveConfig [this] nil)
    (saveDefaultConfig [this] nil)
    (saveResource [this resourcePath replace-b] nil)
    (setNaggable [boolean canNag] nil)))

(defn register-event [pm event-type f]
  (let [listener (reify Listener)
        executor (reify org.bukkit.plugin.EventExecutor
                   (execute [this listener event]
                     (when (instance? event-type event)
                       (try
                         (f event)
                         (catch Exception e (.printStackTrace e))))))
        priority org.bukkit.event.EventPriority/NORMAL]
    (-> pm (.registerEvent event-type listener priority executor dummy-sugot-plugin))))
; End

(defn- register-all-events [plugin-manager]
  (prn 'register-all-events plugin-manager)
  (require 'spifax.app.chat)
  (let [klass org.bukkit.event.player.AsyncPlayerChatEvent
        f #'spifax.app.chat/org.bukkit.event.player.AsyncPlayerChatEvent]
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
  (-> (Bukkit/getServer) (.getPluginManager) (start))
  (catch Exception e (.printStackTrace e)))
