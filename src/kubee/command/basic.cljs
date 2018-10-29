(ns kubee.command.basic
  (:require 
   [clojure.string :as cstr]
   [kubee.chat :as chat])) 

(def sh (js/require "shelljs"))

(defn get-ns [msg]
  (sh.exec
   (str "kubectl get namespaces")
   (fn [code out err]
     (when out
       (chat/reply msg out))
     (when (not= code 0)
       (chat/reply msg (str ":boom: get namespaces error: " err))))))


(defn get-pod [msg [k-ns]]
  (sh.exec
   (str "kubectl get pod -n " k-ns)
   (fn [code out err]
     (when out
      (chat/reply msg out))
     (when (not= code 0)
      (chat/reply msg (str ":boom: get pod error: " err)))))) 
        

(defn get-log [msg [k-ns k-pod k-tail]]
  (when (some nil? [k-ns k-pod k-tail])
    (chat/reply msg "not enough param"))
  (let [log-tail (min 100 k-tail)]
    (prn :ns k-ns :pod k-pod :tail log-tail)
    (sh.exec
     (str "kubectl logs -n " k-ns " --tail=" log-tail " " k-pod)
     (fn [code out err]
       (when out
         (chat/reply msg out))
       (when (not= code 0)
         (chat/reply msg (str ":boom: get log error: " err)))))))    
