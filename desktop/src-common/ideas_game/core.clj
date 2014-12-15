(ns ideas-game.core
  (:require [play-clj.core :refer :all]
            [play-clj.repl :refer [e e! s s!]]
            [play-clj.g2d :refer :all]
            [play-clj.g2d-physics :refer :all]
            [play-clj.ui :refer :all]))

(declare ideas-game game-screen ui-screen)

;; Physics
(def ^:const wall-impact-speed-threshold 24)

;; Config
(def title-location
  {:x (/ 2 (game :width))
   :y (/ 2 (game :height))})

(def first-idea-location
  {:x 50
   :y 50})

;;; Entities
(defn make-idea
  "Create a physics object in the shape of a square, in the center of the map that is rotating clockwise."
  [shape x y]
  (assoc shape
         :x x
         :y y))

(defn split-idea
  "take the first idea and split it into 2, moving in different directions"
  [{:keys [x y] :as entity}])

;; World
(defn out-of-x-bounds
  "FIXME Checks if the provided location is outside the left and right bounds of the screen."
  [screen x]
  (or (< x (:width screen))
      (> x (:x-size screen))))

(defn out-of-y-bounds [screen y]
  "FIXME"
  (or (< x (:height screen))
      (> x (:height screen))))

(defn out-of-bounds [screen x y]
  "Checks if the locations are completely out of bounds on any side."
  (or (out-of-x-bounds screen x)
      (out-of-y-bounds screen y)))

;; FIXME Cull entities that are completely offscreen
#_(defn cull-entities
    "Removes ideas entities which are completely outside the bounds of the stage."
    [screen entities]
    (doall [{:keys [x y] :as entity} entities]
           (if (out-of-bounds screen x y)
             (filter entity entities)
             entities)))

;; Game State
(defscreen title-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (label "Touch To Begin"
           (color :white)
           :set-position (:x title-location) (:y title-location)))

  :on-render
  (fn [screen entities]
    (clear!)
    (if (game :touched?)
      (set-screen! ideas-game game-screen ui-screen))
    (render! screen entities)))

(defscreen game-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (-> (shape :filled
               :set-color (color :white)
               :rect 0 0 100 100)
        (assoc :x (:x first-idea-location)
               :y (:y first-idea-location))))

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities)))

(defscreen ui-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage)))

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities)))

(defgame ideas-game
  :on-create
  (fn [this]
    (set-screen! this title-screen)))


;;;; REPL Stuff
(defn reload! []
  (require 'ideas-game.core :reload)
  (on-gl (set-screen! ideas-game title-screen)))
