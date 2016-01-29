(ns payter.routes.home
  (:require [payter.layout :as layout]
            [payter.api :as api]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [redirect]]
            [ring.util.http-response :refer [ok]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [clojure.java.io :as io]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer [form-to text-field password-field label drop-down hidden-field submit-button]]))

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
     (let [list (api/get-list {:id user-id :pwd password})]
       [:div
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
             [:td (:CardId card)]])]]
        [:button.btn.btn-primary {:data-toggle "modal" :data-target "#add"} "Add a new card"]
        [:div#add.modal.fade
         [:div.modal-dialog.modal-sm
          [:div.modal-content
           (form-to {:class "form-horizontal" :id "form"} [:post "/add-card"]
                    (hidden-field "user-id" user-id)
                    (anti-forgery-field)
                    [:div.modal-header
                     [:button.close {:type "button" :data-dismiss "modal"} "&times;"]
                     [:h4.modal-title "Add a new card"]]
                    [:div.modal-body
                     [:div.form-group
                      [:div.col-sm-12
                       [:div.input-group
                        [:span.input-group-addon
                         [:span.glyphicon.glyphicon-credit-card]]
                        (text-field {:class "form-control"
                                     :maxlength 16
                                     :placeholder "Card number"
                                     :autofocus "autofocus"}
                                    "number")]]]
                     [:div.form-group
                      [:div.col-sm-12
                       [:div.input-group
                        [:span.input-group-addon
                         [:span.glyphicon.glyphicon-user]]
                        (text-field {:class "form-control"
                                     :placeholder "Card holder"}
                                    "holder")]]]
                     [:div.form-group
                      [:div.col-sm-6
                       [:div.input-group
                        [:span.input-group-addon
                         [:span.glyphicon.glyphicon-calendar]]
                        (text-field {:class "form-control"
                                     :maxlength 5
                                     :placeholder "MM/YY"}
                                    "expiration-date")]]
                      [:div.col-sm-6
                       [:div.input-group
                        [:span.input-group-addon
                         [:span.glyphicon.glyphicon-lock]]
                        (text-field {:class "form-control"
                                     :maxlength 4
                                     :placeholder "CVV"}
                                    "cvv")]]]]
                    [:div.modal-footer
                     (submit-button {:class "btn btn-primary col-sm-12"} "Add")])]]]])
     [:p "You did not choose the account correctly"])))

(defn add-card-page [user-id number holder expiration-date cvv session]
  (let [password (get-in session [:accounts user-id])
        [month year] (clojure.string/split expiration-date #"/")
        result (api/add-card {:id user-id
                              :pwd password
                              :number number
                              :month month
                              :year year
                              :holder holder
                              :cvv cvv})]
    ;; TODO: handle payture errors
    (redirect (str "/cards?user-id=" user-id))))

(defn about-page []
  (layout/base :about [:h2 "About payter"]))

(defroutes home-routes
  (GET "/" request (home-page request))
  (GET "/cards" [user-id :as {session :session}] (cards-list-page user-id session))
  (POST "/add-card" [user-id number holder expiration-date cvv :as {session :session}]
        (add-card-page user-id number holder expiration-date cvv session))
  (GET "/about" [] (about-page)))
