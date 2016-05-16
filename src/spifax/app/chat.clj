(ns spifax.app.chat
  (:require [spifax.lib :as l]))

(def ^:private bot-verifier (System/getenv "BOT_VERIFIER"))

(prn :bot-verifier bot-verifier)

(defn org.bukkit.event.player.AsyncPlayerChatEvent [event]
  (let [player (.getPlayer event)]
    (prn 'chat player event)))
