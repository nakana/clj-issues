(ns clj-issues.core1
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clj-issues.data :as data]))

(defn process-with-counter [a b]
  {:data       (conj (:data       a)                     b)
   :number     (max  (:number     a) (-> (:number     b) str count))
   :created_at (max  (:created_at a) (-> (:created_at b)     count))
   :title      (max  (:title      a) (-> (:title      b)     count))})


(defn rpt-str [s n]
  (clojure.string/join (repeat n s)))

(defn make-holizon-line [x]
  (str 
   (rpt-str "-" (inc (:number x)))
   "+"
   (rpt-str "-" (+ 2 (:created_at x)))
   "+"
   (rpt-str "-" (inc (:title x)))))

(defn make-header [x]
  (let [line (make-holizon-line x)
        fmt (format"%%-%ds | %%-%ds | %%-%ds" (:number x) (:created_at x) (:title x))]
    [line (format fmt "number" "created_at" "title") line]))

(defn make-body [x]
  (let [fmt (format"%%-%ds | %%-%ds | %%-%ds" (:number x) (:created_at x) (:title x))]
    (->> (:data x)
         (map #(format fmt (:number %) (:created_at %) (:title %))))))

(defn process [data]
  (-> (json/read-str data :key-fn keyword)
      (->> (map #(select-keys % [:number :title :created_at]))
           (reduce process-with-counter {:data [] :number 6 :created_at 10 :title 5}))
      (as-> x (concat (make-header x) (make-body x)))
      (->> (clojure.string/join "\n"))
      print
      ))

(defn run []
  (let [response @(http/get "https://api.github.com/repos/ruby/ruby/issues")]
    (process (:body response))))
