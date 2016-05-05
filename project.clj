(defproject spifax "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GPL3 or any later versions"
            :url "http://www.gnu.org/licenses/gpl-3.0.en.html"}
  :min-lein-version "2.0.0"
  :repositories {"spigot-repo"
                 "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.0"]
                 [ring/ring-defaults "0.2.0"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler spifax.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
