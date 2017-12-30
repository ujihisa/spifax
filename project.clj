(defproject spifax "0.2.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GPL3 or any later versions"
            :url "http://www.gnu.org/licenses/gpl-3.0.en.html"}
  :min-lein-version "2.8.1"
  :repositories {"spigot-repo"
                 "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
                 #_"raa0121"
                 #_{:url "http://jenkins.raa0121.info/userContent/repository/" :checksum :warn}
                 "localrepo1"
                 "file://myrepo"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [clj-http "3.7.0"]
                 ; I don't know why, but you can't let `sugot` fetch `spigot` and `spigot-api`,
                 ; otherwise you'll get a runtime error at ring bootup
                 [sugot "1.12-SNAPSHOT" :exclusions [com.google.guava/guava
                                                     com.google.code.gson/gson
                                                     clj-http]]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:init spifax.core/init
         :handler spifax.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
