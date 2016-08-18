(ns spifax.app.elytra-boost
  (:require [sugot.lib :as l]))

(defonce players-boostable (atom #{}))

(defn org.bukkit.event.player.PlayerMoveEvent
  "When a player with an Elytra goes through a portal, it acceleates."
  [event]
  'TODO)
