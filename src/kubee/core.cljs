(ns kubee.core 
  (:require [clojure.string :as cstr]))

(def token js/process.env.DISCORD_TOKEN)

(when (cstr/blank? token)
  (prn "Missing token configuration from env.DISCORD_TOKEN")
  (js/process.exit 1))  

(def dc (js/require "discord.js"))
(def client (new dc.Client))

(def sh (js/require "shelljs"))

(defn reply [msg reply-msg]
  (if-not msg.author.bot
    (.send msg.channel reply-msg)
    (prn :not-reply-bot)))

(defn reply-ln [msg text]
  (let [lines  (cstr/split-lines text)]
     (doseq [p (partition 10 10 "" lines)]
        (prn :seq (count p))
        (reply msg (cstr/join "\n" p)))))

(defn reply-2000 [msg text]
  (doseq [ln (partition 1900 1900 [] text)]
    (prn :seq (count ln))
    (reply msg (str "```\n" (cstr/join "" ln) "\n```"))))              

(defn get-pod [msg [k-ns]]
  (prn :ns k-ns)
  (sh.exec
   (str "kubectl get pod -n " k-ns)
   (fn [code out err]
     (when out
      (reply-2000 msg out))
     (when (not= code 0)
      (reply-ln msg (str ":boom: get pod error: " err)))))) 
        

(defn get-log [msg [k-ns k-pod k-tail]]
  (when (some nil? [k-ns k-pod k-tail])
    (reply msg "not enough param"))
  (let [log-tail (min 50 k-tail)]
    (prn :ns k-ns :pod k-pod :tail log-tail)
    (sh.exec 
     (str "kubectl logs -n " k-ns " --tail=" log-tail " " k-pod)
     (fn [code out err]
       (when out
        (reply-2000 msg out))
       (when (not= code 0)
        (reply-ln msg (str ":boom: get log error: " err)))))))    
        

(.on client "ready" #(prn :connected-and-ready))

(.on client "message"
     (fn [msg]
       (let [remove-space (filter #(not= " " %))
             params (-> msg.content
                        (cstr/split " "))
             filtered (filter #(not (cstr/blank? %)) params)
             cmd    (first params)]
         
         (prn :cmd cmd)
         (prn :params filtered)

         (when (= cmd "!!ping")
           (reply msg "!ping"))
     
         (when (= cmd "!ping")
           (reply msg "Pong"))
         
         (when (= cmd "!log")
           (get-log msg (rest filtered)))  
     
         (when (= cmd "!pod")
           (get-pod msg (rest filtered))))))  
           

(defn -main [args] 
  (js/console.log "hello" args)
  (prn :start args)
  (prn :token token)
  (.login client token))

