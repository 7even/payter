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
       xml/parse))

(defn request [endpoint params]
  (let [url (str host "/" endpoint)
        params {:VWID (env :merchant-id)
                :DATA (encode-params params)}
        {:keys [status body]} (get-data url params)]
    (if (= status 200)
      (extract-response body)
      (throw (Exception. (str "Payture returned " status ": " body))))))

(defn get-list [{:keys [id pwd]}]
  (let [params {:VWUserLgn id
                :VWUserPsw pwd}
        data (request "vwapi/GetList" params)]
    (map :attrs (:content data))))

(defn add-card [{:keys [id pwd number month year holder cvv]}]
  (let [params {:VWUserLgn id
                :VWUserPsw pwd
                :CardNumber number
                :CardHolder holder
                :SecureCode cvv
                :EMonth month
                :EYear year}
        data (request "vwapi/Add" params)]
    (:attrs data)))

(defn remove-card [{:keys [id pwd token]}]
  (let [params {:VWUserLgn id
                :VWUserPsw pwd
                :CardId token}
        data (request "vwapi/Remove" params)]
    (:attrs data)))
