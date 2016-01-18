(ns payter.routes.home
  (:require [payter.layout :as layout]
            [payter.api :refer [get-list]]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]
            [hiccup.form :refer [form-to text-field password-field]]))

(defn form [login password]
  (form-to {:class "form-horizontal"} [:get "/list"]
           [:div.form-group
            [:div.col-sm-offset-2.col-sm-10
             [:h4 "Get cards list"]]]
           [:div.form-group
            [:label.col-sm-2.control-label {:for "login"} "Login"]
            [:div.col-sm-10
             (text-field {:class "form-control" :id "login" :placeholder "your login"} "login" login)]]
           [:div.form-group
            [:label.col-sm-2.control-label {:for "password"} "Password"]
            [:div.col-sm-10
             (password-field {:class "form-control" :id "password" :placeholder "your password"} "password" password)]]
           [:div.form-group
            [:div.col-sm-offset-2.col-sm-10
             [:button.btn.btn-default {:type "submit"} "Get cards list"]]]))

(defn home-page []
  (layout/base
   :home
   (form nil nil)))

(defn get-list-page [login password]
  (let [list (get-list {:id login :pwd password})]
    (layout/base
     :home
     (form login password)
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
  (GET "/" [] (home-page))
  (GET "/list" [login password] (get-list-page login password))
  (GET "/about" [] (about-page)))

