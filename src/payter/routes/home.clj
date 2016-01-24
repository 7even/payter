(ns payter.routes.home
  (:require [payter.layout :as layout]
            [payter.api :refer [get-list]]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]
            [hiccup.form :refer [form-to text-field password-field]]))

(defn home-page [{session :session}]
  (let [id (:user-id session)
        password (:user-password session)
        list (get-list {:id id :pwd password})]
    (layout/base
     :home
     [:table.table.table-striped.table-hover
      [:thead
       [:tr
        [:th "Card number"]
        [:th "Card holder"]
        [:th "Status"]
        [:th "No CVV"]
        [:th "Expired"]
        [:th "Token"]]]
      [:tbody
       (for [card list]
         [:tr
          [:td (:CardName card)]
          [:td (:CardHolder card)]
          [:td (:Status card)]
          [:td (:NoCVV card)]
          [:td (:Expired card)]
          [:td (:CardId card)]])]])))

(defn about-page []
  (layout/base :about [:h2 "About payter"]))

(defroutes home-routes
  (GET "/" request (home-page request))
  (GET "/about" [] (about-page)))
