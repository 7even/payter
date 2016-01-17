(ns payter.routes.home
  (:require [payter.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/base :home [:h2 "Hello from hiccup!"]))

(defn about-page []
  (layout/base :about [:h2 "About payter"]))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))

