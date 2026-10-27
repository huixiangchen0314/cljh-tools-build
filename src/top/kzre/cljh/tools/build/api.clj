(ns top.kzre.cljh.tools.build.api
  (:require [clojure.tools.build.api :as b]))

(def create-basis b/create-basis)

(defn compile-cljh [params]
  ((requiring-resolve 'top.kzre.cljh.tools.build.tasks.compile-cljh/compile-cljh) params))