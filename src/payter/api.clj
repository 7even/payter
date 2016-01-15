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
  (let [url (str host "/" endpoint)
        params {:VWID (env :merchant-id)
                :DATA (encode-params params)}
        {:keys [status body]} @(http/get url {:query-params params})]
    (if (= status 200)
      (json/read-str body)
      (throw (Exception. (str "Payture returned " status ": " body))))))

(defn get-list [{:keys [id pwd]}]
  (request "vwapi/GetList"
           {:VWUserLgn id
            :VWUserPsw pwd}))
