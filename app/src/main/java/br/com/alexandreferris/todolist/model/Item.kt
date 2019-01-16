package br.com.alexandreferris.todolist.model

import br.com.alexandreferris.todolist.util.constants.ItemConstans

class Item {
    var id: Long = 0
    var title: String = ""
    var description: String = ""
    var category: String = ""
    var completed: String = ItemConstans.COMPLETED_YES
    var priority: String = ItemConstans.PRIORITY_LOW
    var alarmDateTime: String = "0"

    constructor()

    constructor(id: Long, title: String, description: String, category: String, completed: String, priority: String, alarmDateTime: String) {
        this.id = id
        this.title = title
        this.description = description
        this.category = category
        this.completed = completed
        this.priority = priority
        this.alarmDateTime = alarmDateTime
    }

}