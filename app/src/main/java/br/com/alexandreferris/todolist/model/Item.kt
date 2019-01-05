package br.com.alexandreferris.todolist.model

class Item {
    var id: Long = 0
    var title: String = ""
    var description: String = ""
    var category: String = ""
    var completed: String = "NO"

    constructor()

    constructor(id: Long, title: String, description: String, category: String, completed: String) {
        this.id = id
        this.title = title
        this.description = description
        this.category = category
        this.completed = completed
    }

}