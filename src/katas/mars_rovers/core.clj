(ns katas.mars-rovers.core)

(defn vec+ [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn on-plateau? [[plateau-x plateau-y] [coord-x coord-y]]
  (and (<= 0 coord-x plateau-x)
       (<= 0 coord-y plateau-y)))

(defn right [dir]
  ({:N :E, :E :S, :S :W, :W :N} dir))

(defn left [dir]
  ({:N :W, :W :S, :S :E, :E :N} dir))

(defn dir->vec [dir]
  ({:N [0 1], :E [1 0], :S [0 -1], :W [-1 0]} dir))

(defn new-direction [dir cmd]
  ({:L (left dir), :R (right dir)} cmd dir))

(defn move [plateau dir location]
  (let [potential (vec+ (dir->vec dir) location)]
    (if (on-plateau? plateau potential)
      potential
      location)))

(defn go-rover [plateau {:keys [position instructions]}]
  (loop [dir (last position)
         location (butlast position)
         instr instructions]
    (if (empty? instr)
      {:position [(first location) (second location) dir]}
      (let [cmd (first instr)]
        (recur (new-direction dir cmd)
               (if (= :M cmd) (move plateau dir location) location)
               (rest instr))))))

(defn go!
  [{:keys [plateau rovers]}]
  {:rovers (mapv (partial go-rover plateau) rovers)})

