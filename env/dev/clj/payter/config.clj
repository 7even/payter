(ns payter.config
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [payter.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[payter started successfully using the development profile]=-"))
   :middleware wrap-dev})
