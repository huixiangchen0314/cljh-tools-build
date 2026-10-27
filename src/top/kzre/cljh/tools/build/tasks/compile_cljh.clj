(ns top.kzre.cljh.tools.build.tasks.compile-cljh
  (:require [top.kzre.homunculus.cli.core :as cljh]
            [clojure.tools.build.api :as b]
            [clojure.string :as str])
  (:import (java.io File)))

(defn- absolute-path
  "确保路径字符串为绝对路径"
  [path] (str (b/resolve-path path)))

(defn- list-args [paths]
  (str/join File/pathSeparator paths))

(defn- opts->args
  "
  参数解析
  basic clojure.tools.build
  target 目标编译后端
  macro-namespaces 编译指定的宏命名空间
  output-path 输出路径
  config-files 编译配置文件
  lib-dirs 逐调用的额外库路径
  src-files 入口文件"
  [{:keys [basis
           target
           macro-namespaces
           output-path
           config-files
           lib-dirs
           src-files]
    :as   params}]
  (when-not basis
    (throw (ex-info "cljh: :basis 必填，用 api/create-basis 生成" {:params params})))
  (when-not (seq src-files)
    (throw (ex-info "cljh: :files 至少需要一个输入文件" {:params params})))
  (let [lib-paths
        (->> (concat (:paths basis) lib-dirs (:classpath-roots basis))
             distinct
             (mapv absolute-path))
        macro-namespaces (distinct macro-namespaces)]
    (cond-> []
            true (into ["--target" (str target)])
            (seq lib-paths) (into ["--lib" (list-args lib-paths)])
            (seq macro-namespaces) (into ["--macro-ns" (list-args macro-namespaces)])
            (seq config-files) (into ["--config" (list-args (map absolute-path config-files))])
            (seq output-path) (into ["--output" (absolute-path output-path)])
            true (into (map absolute-path src-files)))))

(defn compile-cljh
  [params]
  (let [args-v (if (vector? params) params (opts->args params))]
    (cljh/compile args-v)
    nil))