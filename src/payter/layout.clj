(ns payter.layout
  (:require [selmer.parser :as parser]
            [selmer.filters :as filters]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.element :refer [link-to]]
            [markdown.core :refer [md-to-html-string]]
            [ring.util.http-response :refer [content-type ok]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]))


(declare ^:dynamic *app-context*)
(parser/set-resource-path!  (clojure.java.io/resource "templates"))
(parser/add-tag! :csrf-field (fn [_ _] (anti-forgery-field)))
(filters/add-filter! :markdown (fn [content] [:safe (md-to-html-string content)]))

(defn render
  "renders the HTML template located relative to resources/templates"
  [template & [params]]
  (content-type
    (ok
      (parser/render-file
        template
        (assoc params
          :page template
          :csrf-token *anti-forgery-token*
          :servlet-context *app-context*)))
    "text/html; charset=utf-8"))

(defn error-page
  "error-details should be a map containing the following keys:
   :status - error status
   :title - error title (optional)
   :message - detailed error message (optional)

   returns a response map with the error page as the body
   and the status specified by the status key"
  [error-details]
  {:status  (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (parser/render-file "error.html" error-details)})

(defn menu [active-page]
  [:ul.nav.navbar-nav
   (for [[url text] [["/" "Home"]
                     ["/about" "About"]
                     ["/sign-in" "Sign in"]]]
     (if (= (clojure.string/capitalize (name active-page))
            text)
       [:li.active (link-to url text)]
       [:li (link-to url text)]))])

(defn base [active-page & content]
  (-> (html5
       [:head
        [:title "Payter"]
        (include-css "/assets/bootstrap/css/bootstrap.min.css"
                     "/assets/bootstrap/css/bootstrap-theme.min.css"
                     "/css/screen.css")
        (include-js "/assets/jquery/jquery.min.js"
                    "/assets/bootstrap/js/bootstrap.min.js"
                    "/assets/bootstrap/js/collapse.js")]
       [:body
        [:div#navbar
         [:nav.navbar.navbar-inverse.navbar-fixed-top
          [:div.container
           [:div.navbar-header
            [:button.navbar-toggle {:data-target "#app-navbar"
                                    :data-toggle "collapse"
                                    :aria-expanded "false"
                                    :aria-controls "navbar"}
             [:span.sr-only "Toggle Navigation"]
             [:span.icon-bar]
             [:span.icon-bar]
             [:span.icon-bar]]
            (link-to {:class "navbar-brand"} "/" "Payter")]
           [:div#app-navbar.navbar-collapse.collapse
            (menu active-page)]]]]

        [:div.container content]])
      ok
      (content-type "text/html; charset=utf-8")))
