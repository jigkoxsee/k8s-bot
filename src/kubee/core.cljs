(ns kubee.core 
  (:require
   [clojure.string :as cstr]
   [kubee.chat :as chat]
   [kubee.command.basic :as basic]))

(def token js/process.env.DISCORD_TOKEN)

(when (cstr/blank? token)
  (prn "Missing token configuration from env.DISCORD_TOKEN")
  (js/process.exit 1))

(def discord (js/require "discord.js"))

(defn get-args [text]
  (let [remove-space (filter #(not= " " %))
        args         (cstr/split text " ")
        filtered     (filter #(not (cstr/blank? %)) args)]
    filtered))


(defn -main [args]
  (prn :starting)
  (let [client (new discord.Client)]
    (.on client "ready" #(prn :connected-and-ready))

    (.on client "message"
        (fn [msg]
          (let [args (get-args msg.content)
                cmd (first args)
                text (rest args)]
          
            (case cmd
              "!ping"      (chat/reply msg "pong")
              "!!ping"     (chat/reply msg "!ping")
              "!ns"        (basic/get-ns msg)
              "!deploy"    (basic/get-deploy msg text)
              "!pod"       (basic/get-pod msg text)
              "!log"       (basic/get-log msg text)
              (prn :unknown-cmd)))))

    (.login client token)))

