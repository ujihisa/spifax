(ns spifax.app.single-sleep-test
  (:require [clojure.test :refer :all]
            [spifax.app.single-sleep :refer :all]
            [sugot.lib :as l]))

(deftest org.bukkit.event.player.PlayerBedEnterEvent*-test
  (with-redefs [l/later-fn (fn [tick f] (f))
                l/sec identity
                l/post-lingr identity
                l/broadcast identity]
    (#'org.bukkit.event.player.PlayerBedEnterEvent*
      :player :world
      (fn get-time [world]
        (assert (= world :world))
        (inc 12541))
      (fn set-time [world time*] (assert (= world :world)
                                         (= time* 0))))))
