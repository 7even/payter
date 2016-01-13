(ns payter.config
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[payter started successfully]=-"))
   :middleware identity})
