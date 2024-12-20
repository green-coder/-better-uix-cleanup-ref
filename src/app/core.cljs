(ns app.core
  (:require [uix.core :as uix :refer [defui $]]
            [uix.dom :as dom]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [uix.re-frame2 :refer [use-subscribe use-track]]
            [app.re-frame-store :as re-frame-store]))

(defn re-frame-section-tracked [text]
  {:from-argument text
   :from-rf-subscribe-soup {
                            ;;:a @(rf/subscribe [::re-frame-store/a])
                            :b @(rf/subscribe [::re-frame-store/b])
                            :c @(rf/subscribe [::re-frame-store/c])
                            :d @(rf/subscribe [::re-frame-store/d])}})

(defui re-frame-section []
  (let [text (use-subscribe [::re-frame-store/text])
        d (use-subscribe [::re-frame-store/d])
        subscription-mess (use-track re-frame-section-tracked text)]
    ($ :<>
       ($ :div
          ($ :input {:onChange #(rf/dispatch [::re-frame-store/set-text (-> % .-target .-value)])
                     :value text})
          " "
          ($ :button {:onClick #(rf/dispatch [::re-frame-store/inc-a])} "Increment number")
          " "
          (str d))
       ($ :div
          ($ :h1 "use-track")
          ($ :pre (with-out-str (cljs.pprint/pprint subscription-mess)))))))



(defui section-toggle [{:keys [id label default-checked children]
                        :or {default-checked false}}]
  (let [[checked set-checked] (uix/use-state default-checked)]
    ($ :section
       ($ :h1
          ($ :input {:id id
                     :type "checkbox"
                     :checked checked
                     :onChange (fn [^js event]
                                 (set-checked (-> event .-target .-checked)))})
          ($ :label {:for id}
             label))
       (when checked
         children))))

(defui app []
  ($ :main
     ($ section-toggle {:id :re-frame, :label "Re-frame"}
        ($ re-frame-section))
     ($ section-toggle {:id :re-frame2, :label "Re-frame 2"}
        ($ re-frame-section))))

(defonce root
  (dom/create-root (js/document.getElementById "root")))

(defn render []
  (dom/render-root ($ app) root))

(defn ^:export init []
  (rf/dispatch-sync [::re-frame-store/init-db])
  (render))
