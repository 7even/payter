(ns payter.routes.auth
  (:require [payter.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response redirect]]
            [ring.util.http-response :refer [ok]]
            [hiccup.form :refer [form-to text-field password-field]]))

(defn form [login password]
  (form-to {:class "form-horizontal"} [:post "/sign-in"]
            [:div.form-group
             [:div.col-sm-offset-2.col-sm-10
              [:h4 "Sign in"]]]
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
              [:button.btn.btn-default {:type "submit"} "Sign in"]]]))

(defn sign-in-page []
  (layout/base
   :sign-in
   (form nil nil)))

(defn submit-sign-in [login password {session :session}]
  (assoc
    (redirect "/" :see-other)
    :session
    (assoc session :user-id login :user-password password)))

(defroutes auth-routes
  (GET "/sign-in" [] (sign-in-page))
  (POST "/sign-in" [login password :as request] (submit-sign-in login password request)))
