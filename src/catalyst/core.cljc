(ns catalyst.core
  #?(:cljs (:require-macros [catalyst.macros :refer [import-vars]]))
  (:require #?@(:clj ([catalyst.macros :refer [import-vars]]))
            [clojure.set :as set]
            [catalyst.funk]
            ))

#?(:cljs (enable-console-print!))

(def ^:dynamic *dbg* false)
(defn dbg
  ([tag x] (when *dbg* (prn tag x)) x)
  ([x] (dbg :dbg x)))

;(println (macroexpand '(import-vars catalyst.funk)))
(import-vars catalyst.funk)
