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
   "Get color palatte going"
   "Add sounds on collision"
   "Add sounds on division"
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
         :y y
         :velocity-x
         :velocity-y
         :change-x
         :change-y))

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

(defn rotate-title-idea [{:keys [angle shape?] :as entity}]
  (if shape?
    (assoc entity :angle (- angle 0.1))
    entity))

;; Game State
(defscreen title-screen
  :on-show
  (fn [screen entities]
    (music "dissonencien.mp3" :play :set-looping true)
    (update! screen :renderer (stage))
    [(image "Ideas.png"
            :set-position (* (game :width) 0.15) (/ (game :height) 1.66))
     (label "Touch Screen To Begin"
            (color :white)
            :set-position (/ (game :width) 3) (/ (game :height) 3.33))
     (image "dissonencien_img.png"
            :set-scale 0.25
            :set-position 5 5)
     (label "Now Playing"
            (color :white)
            :set-position 70 42)
     (label "Dissonencien"
            (color :white)
            :set-position 70 22)
     (label "By Arielle Grimes"
            (color :white)
            :set-position 70 3)
     (assoc (shape :filled
                   :translate -50 -50 0
                   :set-color (color :white)
                   :rect 0 0 100 100)
            :x (/ (game :width) 2)
            :y  (/ (game :height) 2)
            :angle 30
            :shape? true)])

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen
             (map rotate-title-idea entities)))

  :on-touch-down
  (fn [screen entities]
    (set-screen! ideas-game game-screen ui-screen)))

#_(defn get-x-velocity [{:keys [x-velocity]}]
  (if (pos? x-velocity)
    (* - max-velocity)
    x-velocity))

#_(defn get-y-velocity [{:keys [y-velocity]}]
  (if (pos? y-velocity)))

#_(defn move
  [{:keys [delta-time]} {:keys [x y can-jump?] :as entity}]
  (let [x-velocity (get-x-velocity entity)
        y-velocity (get-y-velocity entity)
        x-change (* x-velocity delta-time)
        y-change (* y-velocity delta-time)]
    (if (or (not= 0 x-change)
            (not= 0 y-change))
      (assoc entity)
     entity)))

(defscreen game-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (assoc (shape :filled
                  :set-color (color 244.0 179.0 108.0 1)
                  :rect 0 0 90 90)
           :x 50
           :y 50
           :x-velocity 0
           :y-velocity 0
           :x-change 0
           :y-change 0))

  :on-render
  (fn [screen entities]
    (clear!)
    (let [input-x (:input-x screen)
          input-y (:input-y screen)]
      (->> entities
           #_(map (fn [entity]
                  (->> entity
                       (move screen)
                       (collide screen)
                       (resolve-collisions screen)))
                entities)
           (render! screen))))
  )

(defscreen ui-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage)))

  :on-render
  (fn [screen entities]
    (render! screen entities)))

(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!)))

(defgame ideas-game
  :on-create
  (fn [this]
    (set-screen! this title-screen)))

;;;; REPL Helpers
(defn reload! []
  (require 'ideas-game.core :reload)
  (on-gl (set-screen! ideas-game title-screen)))

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn) (catch Exception e
                                          (.printStackTrace e)
                                          (set-screen! ideas-game blank-screen)))))
