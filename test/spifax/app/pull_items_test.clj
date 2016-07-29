(ns spifax.app.pull-items-test
  (:require [clojure.test :refer :all]
            [sugot.lib :as l]
            [spifax.app.pull-items :refer :all]))

(deftest org.bukkit.event.player.PlayerToggleSneakEvent'-test
  (with-redefs [l/later-fn (fn [tick f] (f))
                l/sec identity
                l/post-lingr identity
                l/broadcast identity]
    (#'org.bukkit.event.player.PlayerToggleSneakEvent'
      :player :player-location
      true
      true
      (fn get-entities-around [player]
        [])
      (fn teleport [entity loc]
        :ok))))
