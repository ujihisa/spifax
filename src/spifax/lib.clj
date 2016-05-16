(ns spifax.lib
  (:import [org.bukkit Bukkit]))

(defn post-lingr-sync [msg]
  (when bot-verifier
    (clj-http.client/post
      "http://lingr.com/api/room/say"
      {:form-params
       {:room "mcujm"
        :bot 'spifax
        :text (ChatColor/stripColor (str msg))
        :bot_verifier bot-verifier}})))

(prn :post-lingr-sync
     (post-lingr-sync "[lib.clj] restarted"))
