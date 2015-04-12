(ns state-talk.slides
  (:use clojure.repl))
;; C-c M-n  switch to current namespace
;;(in-ns 'state-talk.slides)








;; file example
(def file (atom "file contents"))
file
@file
(def hard-link file)
(= file hard-link)
(def before @file)
(swap! file (fn [x] (str x " after update")))
(def after @file)








;; river example
(def great-river (atom [1 2 3 4 5]))
(def mississippi great-river)
(future
  (dotimes [n 120]
    (Thread/sleep 1000)
    (swap! great-river (fn [coll] (map inc coll)))))
