(ns spifax.lib
  (:import [org.bukkit Bukkit]))

(def ^:dynamic *dummy-plugin*
  (delay (-> (Bukkit/getPluginManager) (.getPlugin "dynmap"))))

(defn sec
  "Convert from seconds to ticks"
  [n]
  (long (* 20 n)))

(defn later-fn [tick f]
  (let [f* (fn []
             (try
               (f)
               (catch Exception e (.printStackTrace e))))]
    (.runTaskLater
      (Bukkit/getScheduler) @*dummy-plugin* f* tick)))

(defmacro later [tick & exps]
  `(later-fn ~tick (fn [] ~@exps)))
