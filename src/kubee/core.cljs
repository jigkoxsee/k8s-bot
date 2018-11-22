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

              "!help"      (basic/get-help msg)
              "!kns"       (basic/get-ns msg)
              "!kgd"       (basic/get-deploy msg text)
              "!kgp"       (basic/get-pod msg text)
              "!kgs"       (basic/get-service msg text)
            
              "!kdn"       (basic/describe-ns msg text)
              "!kdd"       (basic/describe-deploy msg text)

              ;; kdeld - delete deployment
              "!kdeld"     (basic/delete-deploy msg text)
              "!kdelp"     (basic/delete-pod msg text)
              ;; kdels - delete service
            
              "!klf"       (basic/get-log msg text)

              (prn :unknown-cmd)))))

    (.login client token)))

