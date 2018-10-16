(ns kubee.core)

(def token js/process.env.DISCORD_TOKEN)

(def dc (js/require "discord.js"))
(def client (new dc.Client))

(def sh (js/require "shelljs"))

(defn reply [msg reply-msg]
  (if-not msg.author.bot
    (.send msg.channel reply-msg)
    (prn :not-reply-bot)))

(.on client "ready" #(prn :connected-and-ready))

(.on client "message"
     (fn [msg]

       (when (= msg.content "!!ping")
         (reply msg "!ping"))

       (when (= msg.content "!ping")
         (reply msg "Pong"))

       (when (= msg.content "pod")
         (sh.exec
           "kubectl get pod"
           (fn [code out err]
             (let [lines  (clojure.string/split-lines out)]
               (doseq [p (partition 10 10 "" lines)]
                 (prn :seq (count p))
                 (reply msg (clojure.string/join "\n" p)))))))

       (prn :msg (js/Object.keys msg))))

(defn -main [args] 
  (js/console.log "hello" args)
  (prn :start args)
  (prn :token token)
  (.login client token))

