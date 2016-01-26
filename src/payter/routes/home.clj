(ns payter.routes.home
  (:require [payter.layout :as layout]
            [payter.api :refer [get-list]]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer [form-to label drop-down submit-button]]))

(defn account-selector [session user-id]
  (if-let [accounts (-> session :accounts keys)]
    (form-to {:class "form-inline"} [:get "/cards"]
             [:div.form-group
              (drop-down {:class "form-control"}
                         "user-id"
                         (into [] accounts)
                         user-id)
              (submit-button {:class "btn btn-default"} "See cards")])
    [:p (link-to "/sign-in" "Sign in") " to see the cards list."]))

(defn home-page [{session :session}]
  (layout/base
   :home
   (account-selector session nil)))

(defn cards-list-page [user-id session]
  (layout/base
   nil
   (account-selector session user-id)
   (if-let [password (get-in session [:accounts user-id])]
     (let [list (get-list {:id user-id :pwd password})]
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
            [:td (:CardId card)]])]])
     [:p "You did not choose the account correctly"])))

(defn about-page []
  (layout/base :about [:h2 "About payter"]))

(defroutes home-routes
  (GET "/" request (home-page request))
  (GET "/cards" [user-id :as {session :session}] (cards-list-page user-id session))
  (GET "/about" [] (about-page)))
