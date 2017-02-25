(def complex {:name "Root"
              :children [{:name "First Child"
                          :children [{:name "First Grandchild"}
                                     {:name "Second Grandchild"}]}
                         {:name "Second Child"}]})

(def lchildren
  {:get (fn [source] (:children source))
   :put (fn [source updated-view] (assoc source :children updated-view))})

; or, more clearly
(def lchildren
  {:get :children                 ; takes in source, returns "zoomed in" value
   :put #(assoc % :children %2)}) ; takes in source and updated value, returns modified sourcd

(def lfirst
  {:get first
   :put #(conj (rest %) %2)})

; gets work
((:get lchildren) complex)
((:get lfirst) ((:get lchildren) complex))

; puts work
((:put lchildren) complex "stuff")
((:put lfirst) [:1st :2nd] :changed)

; compose gets
(->> complex
     ((:get lchildren))
     ((:get lfirst))
     ((:get lchildren)))

; compose puts
                                         ((:get lchildren) complex)
                          ((:put lfirst) ((:get lchildren) complex) :changed)
((:put lchildren) complex ((:put lfirst) ((:get lchildren) complex) :changed))

; compose lenses
(defn lens-comp [outer inner]
  {:get (comp (:get inner) (:get outer))
   :put (fn [source updated-view]
          (let [outer-put (:put outer)
                inner-put (:put inner)
                outer-get (:get outer)]
            (outer-put source (inner-put (outer-get source) updated-view))))})

(def lfirst-child (lens-comp lchildren lfirst))
((:get lfirst-child) complex)

(def lfirst-grandchild (reduce lens-comp [lchildren lfirst lchildren lfirst]))
((:get lfirst-grandchild) complex)

; feels like get-in, update-in, and assoc-in







(def src "<person><fname>Archibald</fname><lname>Buttle</lname><other>...</other></person>")

(def xml-to-map
  {:get (fn [source]
          {:fname (second (re-find #"<fname>(.*)</fname>" source))
           :lname (second (re-find #"<lname>(.*)</lname>" source))})
   :put (fn [source updated-view]
          (-> source
              (clojure.string/replace #"<fname>(.*)</fname>" (str "<fname>" (:fname updated-view) "</fname>"))
              (clojure.string/replace #"<lname>(.*)</lname>" (str "<lname>" (:lname updated-view) "</lname>"))))})

((:get xml-to-map) src)
((:put xml-to-map) src {:fname "Archibald" :lname "Tuttle"})

; <other>...</other> hasn't changed
