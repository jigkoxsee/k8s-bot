(ns kubee.command.basic
  (:require 
   [clojure.string :as cstr]
   [kubee.chat :as chat])) 

(def sh (js/require "shelljs"))

(defn get-help [msg]
      (chat/reply msg (str "usage/available commands:\n
      !ping\t\t\t\tping to chatbot\n
      !!ping\t\t\t\tget how to ping message\n
      !kns\t\tlist namespaces\n
      !kgd <namespace>\t\t\t\tlist deployment in namespace\n
      !kgp <namespace>\t\t\t\tlist pod in namespace\n
      !kgs <namespace>\t\t\t\tlist service in namespace\n
      \n
      !kdn [namespace]\t\t\t\t describe namespaces\n
      !kdd <namespace> [deployment]\t\t\t\tdescribe deployment\n

      !kdeld <namespace> [deployment]\t\t\t\tdelete deployment\n
      !kdelp <namespace> [deployment]\t\t\t\tdelete deployment\n
      ")))

(defn get-ns [msg]
  (sh.exec
   (str "kubectl get namespaces")
   (fn [code out err]
     (when out
       (chat/reply msg out))
     (when (not= code 0)
       (chat/reply msg (str ":boom: get namespaces error: " err))))))


(defn get-deploy [msg [k-ns]]
  (sh.exec
   (str "kubectl get deployments -n " k-ns)
   (fn [code out err]
     (when out
       (chat/reply msg out))
     (when (not= code 0)
       (chat/reply msg (str ":boom: get deployments error: " err))))))


(defn get-service [msg [k-ns]]
  (sh.exec
   (str "kubectl get services -n " k-ns)
   (fn [code out err]
     (when out
       (chat/reply msg out))
     (when (not= code 0)
       (chat/reply msg (str ":boom: get service error: " err))))))


(defn get-pod [msg [k-ns]]
  (sh.exec
   (str "kubectl get pod -n " k-ns)
   (fn [code out err]
     (when out
      (chat/reply msg out))
     (when (not= code 0)
      (chat/reply msg (str ":boom: get pod error: " err)))))) 


(defn describe-ns [msg [k-ns]]
  (sh.exec
   (str "kubectl describe namespace " k-ns)
   (fn [code out err]
     (when out
       (chat/reply msg out))
     (when (not= code 0)
       (chat/reply msg (str ":boom: describe namespace error: " err))))))


(defn describe-deploy [msg [k-ns k-deploy]]
  (sh.exec
   (str "kubectl describe deployments -n " k-ns " " k-deploy)
   (fn [code out err]
     (when out
       (chat/reply msg out))
     (when (not= code 0)
       (chat/reply msg (str ":boom: describe deploy error: " err))))))


(defn delete-deploy [msg [ns deploy]]
  (chat/reply msg "deleting...")
  (sh.exec
   (str "kubectl delete deploy -n " ns " " deploy)
   (fn [code out err]
     (when out
       (chat/reply msg out))
     (when (not= code 0)
       (chat/reply msg (str ":boom: delete deploy error: " err))))))


(defn delete-pod [msg [ns pod]]
  (chat/reply msg "deleting...")
  (sh.exec
   (str "kubectl delete pod -n " ns " " pod)
   (fn [code out err]
     (when out
       (chat/reply msg out))
     (when (not= code 0)
       (chat/reply msg (str ":boom: delete pod error: " err))))))


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
