(ns payter.mock-responses
  (:require [clojure.string :as str]))

(def dir "mock_responses")

(defn path [name]
  (let [resource-path (str dir "/" name ".xml")
        resource (clojure.java.io/resource resource-path)]
    (str resource)))

(defn mock-response [url]
  (let [name (-> url
                 (str/split #"/")
                 last
                 (str/replace #"([a-z])([A-Z])" "$1_$2")
                 str/lower-case)]
    (slurp (path name))))
