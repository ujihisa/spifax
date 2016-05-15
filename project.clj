(defproject spifax "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GPL3 or any later versions"
            :url "http://www.gnu.org/licenses/gpl-3.0.en.html"}
  :min-lein-version "2.5.0"
  :repositories {"org.bukkit"
                 "http://repo.bukkit.org/content/groups/public/"
                 "spigot-repo"
                 "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
                 "localrepo1"
                 "file://myrepo"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.0"]
                 [ring/ring-defaults "0.2.0"]
                 [org.spigotmc/spigot-api "1.9.4-R0.1-SNAPSHOT"]
                 [org.spigotmc/spigot "1.9.4"]
                 [clj-http "3.0.1"]
                 [sugot "1.0" :exclusions [com.google.guava/guava
                                           com.google.code.gson/gson
                                           clj-http]]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:init spifax.core/init
         :handler spifax.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
