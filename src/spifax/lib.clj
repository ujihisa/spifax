(ns spifax.lib
  (:require [sugot.lib])
  (:import [org.bukkit Bukkit ChatColor]))

(defn post-lingr-sync [msg]
  (when @#'sugot.lib/bot-verifier
    (clj-http.client/post
      "http://lingr.com/api/room/say"
      {:form-params
       {:room "mcujm"
        :bot 'spifax
        :text (ChatColor/stripColor (str msg))
        :bot_verifier @#'sugot.lib/bot-verifier}})))

(defn post-lingr [msg]
  (future (post-lingr-sync msg)))
