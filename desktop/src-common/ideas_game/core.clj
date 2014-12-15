(ns ideas-game.core
  (:require [play-clj.core :refer :all]
            [play-clj.repl :refer [e e! s s!]]
            [play-clj.g2d :refer :all]
            [play-clj.g2d-physics :refer :all]
            [play-clj.ui :refer :all]))

(declare ideas-game game-screen ui-screen)

(def todos
  ["receive coords from touch and mouse events"
   "box avoids pointer"
   "collide with walls"
   "collide with other squares"
   "duplicate on touch"
   "rotate from collision"
   "be able to be pushed off of screen"
   "add gradient background to make it more brainly"])

;; FIXME Phys values. they're not tuned yet
(def ^:const wall-impact-speed-threshold 24)
(def ^:const damping 4)
(def ^:const friction 4)

(def ^:const idea-react-distance 40)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;; Entities
(defn make-idea
  "Create a physics object in the shape of a square, in the center of the map that is rotating clockwise."
  [shape x y]
  (assoc shape
         :x x
         :y y))

(defn dupe-idea
  "take the first idea and split it into 2, moving in different directions depending on where the idea was touched"
  [{:keys [x y] :as entity}])

;; World
(defn out-of-x-bounds
  "Checks if the provided location is outside the left and right bounds of the screen.
  FIXME width of the entity need to factor in for the bounds check for neg"
  [screen x]
  (or (< x (game :width))
      (neg? x)))

(defn out-of-y-bounds [screen y]
  "FIXME fix the math here 
  FIXME width of the entity need to factor in for the bounds check for neg"
  (or (< y (game :height))
      (neg? y)))

(defn out-of-bounds [screen x y]
  "FIXME: factor rotation into out-of-bounds?"
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
    #_(clear! 248.0 244.0 215.0 1)
    (let [title-x (- (/ (game :width) 2) 10)
          title-y (/ (game :height) 2)
          label-x (- (/ (game :width) 2) 40)
          label-y (/ (game :height) 2.2)]

      [(label "Ideas"
             (color 244.0 179.0 108.0 1)
             :set-scale 24
             :set-position title-x title-y)
       (label "Touch To Begin"
             (color :white)
             :set-position label-x label-y)]))

  :on-render
  (fn [screen entities]
    (clear! 248.0 244.0 215.0 1)
    (render! screen entities))

  :on-touch-down
  (fn [screen entities]
    (if (game :touched?)
      (set-screen! ideas-game game-screen ui-screen))))

(defscreen game-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (assoc (shape :filled
                  :set-color (color 244.0 179.0 108.0 1)
                  :rect 0 0 90 90)
           :x 50
           :y 50))

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
    (render! screen entities)))

(defgame ideas-game
  :on-create
  (fn [this]
    (set-screen! this title-screen)))

;;;; REPL Helpers
(defn reload! []
  (require 'ideas-game.core :reload)
  (on-gl (set-screen! ideas-game title-screen)))
