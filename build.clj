(ns build
      (:require [clojure.tools.build.api :as b])
      (:import (java.io File)
               (java.nio.file CopyOption Files)))


(def jar-file "target/cljh-tools-build-0.1.0.jar")
(def class-dir "target/classes")

(defn clean [_]
      (b/delete {:path "target"}))

(defn- copy-clj-sources []
       (let [src-dir (File. "src")
             target-dir (File. class-dir)]
            (when (.exists src-dir)
                  (doseq [^File f (file-seq src-dir)
                          :when (and (.isFile f) (.endsWith (.getName f) ".clj"))]
                         (let [rel-path (-> (.toPath src-dir) (.relativize (.toPath f)))
                               dest (File. target-dir (.toString rel-path))]
                              (.mkdirs (.getParentFile dest))
                              (Files/copy (.toPath f) (.toPath dest)
                                          (make-array CopyOption 0)))))))

(defn jar [_]
      (clean nil)
      ;(compile-java nil)
      (copy-clj-sources)
      (b/copy-dir {:src-dirs [ "resources"] :target-dir class-dir})
      (b/jar {:class-dir class-dir
              :jar-file jar-file}))
