(ns ideas-game.core
  (:require #_[ideas-game.entities :as e]
            [ideas-game.utils :as u]
            [play-clj.core :refer :all]
            [play-clj.repl :refer [e e! s s!]]
            [play-clj.g2d :refer :all]
            [play-clj.g2d-physics :refer :all]
            [play-clj.ui :refer :all]))

(declare ideas-game game-screen ui-screen)

(def todos
  ["box avoids pointer"
   "collide with walls"
   "collide with other squares"
   "Get color palatte going"
   "Add sounds on collision"
   "Add sounds on division"
   "duplicate on touch"
   "rotate from collision"
   "be able to be pushed off of screen"
   "add gradient background to make it more brainly"])

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

(defn split
  "take the first idea and split it into 2, moving in different directions depending on where the idea was touched"
  [{:keys [x y] :as entity} entities]
  (conj entities (assoc entity
                        :x (+ x 30)
                        :y (+ x 30))))

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

(defn rotate-all [{:keys [angle shape?] :as entity}]
  (if shape?
    (assoc entity :angle (- angle 0.13))
    entity))

;; Game State
(defscreen title-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))

    (music "dissonencien.mp3" :play :set-looping true)

    [(image "Ideas.png"
            :set-position (* (game :width) 0.15) (/ (game :height) 1.66))
     (label "Touch this Idea to Begin"
            (color :white)
            :set-position (/ (game :width) 3.1) (/ (game :height) 3.33))
     (image "dissonencien_img.png"
            :set-scale 0.25
            :set-position 15 15)
     (label "Now Playing"
            (color :white)
            :set-position 83 52)
     (label "Dissonencien"
            (color :white)
            :set-position 83 32)
     (label "By Arielle Grimes"
            (color :white)
            :set-position 83 13)
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
    (render! screen (map rotate-all entities)))

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

(defn is-touching?
  [input-x input-y {:keys [x y width height] :as entity}]
  (and (and (> input-x x) (< input-x (+ x width)))
       (and (> input-y y) (< input-y (+ y height)))))

(defn handle-touch
  [screen entity]
  (if (game :touched?)
    (if (is-touching? (game :x) (game :y) entity)
      ((fn [] (println "split") entity))
      ((fn [] (println "repel") entity)))
    entity))

(defscreen game-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))

    (assoc (shape :filled
                  :translate -50 -50 0
                  :set-color (color :white)
                  :rect 0 0 100 100)
            :x (/ (game :width) 2)
            :y  (/ (game :height) 2)
            :width 100
            :height 100
            :shape? true))

  :on-render
  (fn [screen entities]
    (clear!)

    (->> entities
         (map (fn [entity]
                (->> entity
                     (handle-touch screen)
                     #_(move screen)
                     #_(collide screen)
                     #_rotate-all)))
         (render! screen))))

(defscreen ui-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage)))

  :on-render
  (fn [screen entities]
    (render! screen entities)))

(defscreen blank-screen
  :on-render
  (fn [screen entites]
    (clear!)))

(defgame ideas-game
  :on-create
  (fn [this]
    (set-screen! this title-screen)))

;;;; REPL Helpers
(defn reload! []
  (require 'ideas-game.core :reload)
  (on-gl (set-screen! ideas-game title-screen)))

;; Disables game screen on exception.
(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn) (catch Exception e
                                          (.printStackTrace e)
                                          (set-screen! ideas-game blank-screen)))))
