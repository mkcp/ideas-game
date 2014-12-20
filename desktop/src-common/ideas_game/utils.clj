(ns ideas-game.utils
  (:require [play-clj.core :refer :all]))

;; FIXME Phys values. not tuned yet
(def ^:const wall-impact-speed-threshold 24)
(def ^:const damping 4)
(def ^:const friction 4)
(def ^:const idea-react-distance 40)
