(ns catalyst.macros-spec
  (:require [untangled-spec.core :refer
             [specification behavior assertions when-mocking]]
            [catalyst.macros :as src]
            catalyst.funk))

(def funk-syms
  (map first (ns-interns 'catalyst.funk)))

(specification "import-vars"
  (behavior "in clj interns vars found by ns-interns"
    (let [syms (atom [])]
      (when-mocking
        (intern _ k _) => (swap! syms conj k)
        (assertions
          (do (src/import-vars catalyst.funk)
              @syms)
          => funk-syms))))
  (behavior "in cljs defs symbols found in the analyzers' ns-interns"
    (let [syms (atom [])]
      (when-mocking
        (src/cljs-env? _) => true
        (cljs.analyzer.api/ns-interns -ns-) => (->> (ns-interns -ns-)
                                                 (mapv #(update % 1 meta)))
        (assertions
          (rest (macroexpand '(catalyst.macros/import-vars catalyst.funk)))
          => (mapv #(vector 'def % (symbol "catalyst.funk" (name %)))
                   funk-syms))))))
