(ns payter.api
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]]))

(def host (str "https://" (env :payture-host)))

(defn encode-params [params]
  (->> params
       (map (fn [[k v]] (str (name k) "=" v)))
       (clojure.string/join ";")))

(defn request [endpoint params]
  (let [{:keys [status body]} @(http/get (str host "/" endpoint) {:query-params {:data (encode-params params)}})]
    (if (= status 200)
      (json/read-str body)
      "error")))
