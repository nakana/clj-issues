(ns clj-issues.core3
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clj-issues.data :as data]))

(defn count-up [a b]
  {:number     (max  (:number     a) (-> (b "number"    ) str count))
   :created_at (max  (:created_at a) (-> (b "created_at")     count))
   :title      (max  (:title      a) (-> (b "title"     )     count))})

(defn rpt-str [s n]
  (apply str (repeat n s)))

(defn item-line-str [data {:keys [number created_at title]}]
  (let [fmt (format"%%-%ds | %%-%ds | %%-%ds" number created_at title)]
      (format fmt (data "number") (data "created_at") (data "title"))))

(defn make-holizon-line [{:keys [number created_at title]}]
  (str (rpt-str "-" (+ 1 number))
       "+"
       (rpt-str "-" (+ 2 created_at))
       "+"
       (rpt-str "-" (+ 1 title))))

(defn make-header-items [x]
  (item-line-str
   {"number" "number" "created_at" "created_at" "title" "title"}
   (select-keys x [:number :created_at :title])))

(defn make-body [x]
  (map #(item-line-str % (select-keys x [:number :created_at :title])) (:data x)))

(defn process [data]
  (-> (json/read-str data)
      (->> (map #(select-keys % ["number" "title" "created_at"])))
      (as-> x (assoc {} :data x))
      (as-> x (merge x (reduce count-up {:number 6 :created_at 10 :title 5} (:data x))))
      (as-> x (concat [(make-holizon-line x) (make-header-items x) (make-holizon-line x)] (make-body x)))
      (->> (clojure.string/join "\n"))
      print))

(defn run []
  (let [response @(http/get "https://api.github.com/repos/ruby/ruby/issues")]
    (process (:body response))))

#_(defn -main [& args]
  (parse-opts args cli-options))
