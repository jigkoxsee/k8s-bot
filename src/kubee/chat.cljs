(ns kubee.chat
  (:require [clojure.string :as cstr]))


(defn _reply [msg reply-msg]
  (if-not msg.author.bot
    (.send msg.channel reply-msg)
    (prn :not-reply-bot)))


(defn reply [msg text]
  (doseq [ln (partition 1990 1990 [] text)]
    (_reply msg (str "```\n" (cstr/join "" ln) "\n```"))))              
