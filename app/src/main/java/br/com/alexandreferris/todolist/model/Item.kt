package br.com.alexandreferris.todolist.model

class Item {
    var id: Long = 0
    var title: String = ""
    var description: String = ""
    var category: String = ""

    constructor()

    constructor(id: Long, title: String, description: String, category: String) {
        this.id = id
        this.title = title
        this.description = description
        this.category = category
    }

}