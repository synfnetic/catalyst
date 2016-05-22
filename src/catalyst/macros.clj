(ns catalyst.macros)

(defn cljs-env?
  "https://github.com/Prismatic/schema/blob/master/src/clj/schema/macros.clj"
  [env] (boolean (:ns env)))

(defn import* [k vr ns-sym cljs?]
  (if cljs?
    (when-not (:macro vr)
      `(def ~(symbol k)
         ~(symbol (name ns-sym)
                  (name k))))
    `(when (.hasRoot ~vr)
       (intern *ns* (with-meta '~k
                      (merge (meta ~vr)
                             (meta '~k)))
               @~vr))))

(defmacro import-vars [& nss]
  (let [cljs? (cljs-env? &env)
        ns-interns* (if cljs?
                      cljs.analyzer.api/ns-interns
                      ns-interns)
        parse-nss (map #(cond-> %
                          (symbol? %) #_:>> (vector :all)
                          (and (sequential? %)
                               (sequential? (second %))) #_:>> (update 1 set)))
        step (mapcat (fn [[ns-sym to-import]]
                       (sequence
                         (comp (filter #(or (= to-import :all) (to-import %)))
                               (map (fn [[k vr]] (import* k vr ns-sym cljs?))))
                         (ns-interns* ns-sym))))]
    `(do ~@(sequence (comp parse-nss step) nss))))
