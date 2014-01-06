(defproject small-repair "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :repositories {"local" "http://192.168.2.199:8002/nexus-2.0.5/content/groups/public"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.3"]
                 [compojure "1.1.5"]
                 [korma "0.3.0-RC4"]
                 [org.clojars.kbarber/postgresql "9.2-1002.jdbc4"]
                 [ring/ring-json "0.2.0"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [org.clojure/tools.logging "0.2.6"]
                 [com.taoensso/carmine "2.1.0"]
                 [org.apache.lucene/lucene-core "4.0.0"]
                 [lq/lucene-queryparser "4.2.0"]
                 [clj-http "0.7.6"]
                 [IK/IK "2012"]
                 [me.raynes/fs "1.4.4"]
                 [log4j/log4j "1.2.16" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jdmk/jmxtools
                                                    com.sun.jmx/jmxri]]]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler small-repair.handler/app
         :init small_repair.init.statistics/init}
  :main small-repair.handler
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}})
