(ns todo.core (:gen-class) (:use compojure.core
                    [compojure.handler :as handler]
                    [ring.middleware.session.memory :as memory]
                    hiccup.core
                    hiccup.form
                    hiccup.page
                    ring.adapter.jetty
                    ring.middleware.file-info
                    ring.middleware.file
                    ring.middleware.session
                    ring.middleware.params))

(defn include-all []
  (html
    [:html
     [:head
      (include-js "/jquery-1.11.2.js")
      (include-js "/todo.js")
      (include-css "/style.css")
      ]]))

(defn delete-icon [css-class]
  (html [:div {:class :cell}
         [:input {:class css-class
                  :type :image
                  :src "/ic_delete.png"}]]))

(defn render-task [task]
  (html
    [:div {:class :row}
     [:div {:class :cell}
      [:input {:class :checkbox :type :checkbox}]
      [:span {:class :task} task]]
     [:div {:class "cell right"}
      (delete-icon :delete-btn)]]))

(defn render-project [proj tasks]
  (html
     [:div {:class :row}
      [:div {:class :proj-title} proj]
      (delete-icon :delete-btn-proj)]
    [:div {:class :todo-list}
    (form-to {:class "addtaskform header"} [:post "/list" ]
             [:div {:class :table}
              [:div {:class :cell} (text-field :task)]
              [:input {:type :hidden
                       :value proj
                       :id :proj
                       :name :proj}]
              [:div {:class :cell} [:input {:class :add-task, :type :submit}]]]
             )
    [:div {:class "project"}
     [:input {:id :proj :type :hidden :value proj}]
     (map render-task tasks)]]))

(defn get-tasks
  []
  (if (.exists (new java.io.File "tasks"))
    (read-string (slurp "tasks"))
    {"default project" []}))

(defn set-tasks
  [tasks]
  (spit "tasks" tasks))

(defn render-all-projects []
  (map (fn [[k v]]
         (render-project k v))
       (get-tasks)))

(defn render-list []
  {:body (html
           (include-all)
           [:body {:class :grad}
            [:div {:class :center}
             (render-all-projects)]
              [:input {:type :button
                       :class :add-todo-list
                       :value "Add TODO List"}]
             ])
   :session {:tasks {}}})


(defn new-task [project task session]
  (let [projects (get-tasks)
        tasks (projects project)]
    (if (neg? (.indexOf tasks task))
      (do (set-tasks (merge-with conj projects {project task}))
        {:body (render-task task)
         :session session})
      {:body ""
       :session session})))

(defn delete
  [proj task]
  (let [all-tasks (get-tasks)
        tasks (all-tasks proj)
        result (filterv (partial not= task) tasks)]

    (set-tasks (merge all-tasks {proj result}))

    {:body "ok"}))

(defn new-todo-list
  []
  (let [projects (get-tasks)
        proj-name (loop [pref "New Project"
                         i 1]
                    (let [new-proj-name (str pref i)]
                      (if (contains? projects new-proj-name)
                        (recur pref (inc i))
                        new-proj-name)))]
    (set-tasks (merge projects {proj-name []}))
    {:body (render-project proj-name [])}))

(defn delete-project [proj]
  (set-tasks (dissoc (get-tasks) proj))
  {:body ""})


(defroutes myroutes
           (GET "/" [] (str "display"))
           (GET "/list" [] (render-list))
           (POST "/list"
                 {params :params, session :session}
                 (new-task (params :proj) (params :task) session))
           (POST "/delete"
                 {params :params session :session}
                 (delete (params :proj) (params :task)))
           (POST "/add-todo-list" {} (new-todo-list))
           (POST "/delete-project" {params :params}
                 (delete-project (params :proj)))
           )

(def app (->
           (handler/site myroutes)
           (wrap-session)
           (wrap-file "files")
           wrap-file-info))

(defonce server (run-jetty app
                           {:join? false
                            :port 1234}))

(defn -main [& args])
