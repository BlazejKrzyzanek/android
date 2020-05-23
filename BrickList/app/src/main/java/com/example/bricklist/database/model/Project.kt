package com.example.bricklist.database.model

class Project {
    var id: Int = 0
    var name: String? = null
    var number: String? = null
    var active: Int = 0
    var lastAccessed: Int = 0

    constructor(id: Int, name: String, number: String, active: Int, lastAccessed: Int) {
        this.id = id
        this.name = name
        this.number = number
        this.active = active
        this.lastAccessed = lastAccessed
    }

    constructor(name: String, number: String, active: Int, lastAccessed: Int) {
        this.name = name
        this.number = number
        this.active = active
        this.lastAccessed = lastAccessed
    }
}
