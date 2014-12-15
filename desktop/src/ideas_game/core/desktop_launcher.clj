(ns ideas-game.core.desktop-launcher
  (:require [ideas-game.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. ideas-game "ideas-game" 450 800)
  (Keyboard/enableRepeatEvents true))
