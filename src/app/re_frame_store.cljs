(ns app.re-frame-store
  (:require [re-frame.core :as rf]))

(rf/reg-event-db ::init-db
  (fn [_ _]
    {:text "world!"
     :a 1}))

(rf/reg-event-db ::inc-a
  (fn [db _]
    (update db :a inc)))

(rf/reg-sub ::a
  (fn [db _]
    (prn (str "compute " ::a))
    [(:a db)]))

(rf/reg-sub ::b
  :<- [::a]
  (fn [a _]
    (prn (str "compute " ::b))
    (conj a :b)))

(rf/reg-sub ::c
  :<- [::a]
  (fn [a _]
    (prn (str "compute " ::c))
    (conj a :c)))

;; Diamond shaped subscription. This is the bottom node.
(rf/reg-sub ::d
  :<- [::b]
  :<- [::c]
  (fn [[b c] _]
    (prn (str "compute " ::d)) ;; This is printed only once each time we click on the button
    [b :d c]))

(rf/reg-event-db ::set-text
  (fn [db [_ text]]
    (assoc db :text text)))

(rf/reg-sub ::text
  (fn [db _]
    (prn (str "compute " ::text))
    (:text db)))
