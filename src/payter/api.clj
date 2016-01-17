(ns payter.api
  (:require [org.httpkit.client :as http]
            [clojure.xml :as xml]
            [environ.core :refer [env]]
            [payter.mock-responses :refer [mock-response]]))

(def host (str "https://" (env :payture-host)))

(defn encode-params [params]
  (->> params
       (map (fn [[k v]] (str (name k) "=" v)))
       (clojure.string/join ";")))

(defn get-data [url params]
  (if (:mock-responses? env)
    {:status 200 :body (mock-response url)}
    @(http/get url {:query-params params})))

(defn extract-response [raw-response-body]
  (->> raw-response-body
       .getBytes
       java.io.ByteArrayInputStream.
       xml/parse
       :content
       (map :attrs)))

(defn request [endpoint params]
  (let [url (str host "/" endpoint)
        params {:VWID (env :merchant-id)
                :DATA (encode-params params)}
        {:keys [status body]} (get-data url params)]
    (if (= status 200)
      (extract-response body)
      (throw (Exception. (str "Payture returned " status ": " body))))))

(defn get-list [{:keys [id pwd]}]
  (request "vwapi/GetList"
           {:VWUserLgn id
            :VWUserPsw pwd}))
