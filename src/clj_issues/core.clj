(ns clj-issues.core
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(http/get
 "https://api.github.com/repos/ruby/ruby/issues"
 (fn [{:keys [body]}]
   (-> (json/read-str body :key-fn keyword)
       (->> (map #(format "%-7s | %-20s | %-40s\n" (:number %) (:created_at %) (:title %))))
       clojure.string/join
       println
       )))






















