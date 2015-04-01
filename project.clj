(defproject todo "0.1.0-SNAPSHOT"
            :min-lein-version "2.5.0"
            :main todo.core
            :ring {:handler todo.core/app}
            :description "A Simple clojure tasklist"
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojure-contrib "1.2.0"]
                           [compojure "1.3.2"]
                           [hiccup "1.0.5"]
                           [ring/ring "1.3.2"]
                           [ring/ring-servlet "1.3.2"]
                           [ring/ring-jetty-adapter "1.2.2"]
                           [environ "0.5.0"]]
            :plugins [[environ/environ.lein "0.2.1"]
                      [lein-ring "0.8.13"]]
            :hooks [environ.leiningen.hooks]
            ;:uberjar-name "clojure-getting-started-standalone.jar"
            :profiles {:production {:env {:production true}}})
