package com.example.bricklist.database.model

class InventoryTO {
    var id: Int = 0
    var name: String? = null
    var active: Int = 0
    var lastAccessed: Long = 0

    constructor() {
    }


    constructor(name: String, active: Int, lastAccessed: Long) {
        this.name = name
        this.active = active
        this.lastAccessed = lastAccessed
    }

    constructor(id: Int, name: String?, active: Int, lastAccessed: Long) {
        this.id = id
        this.name = name
        this.active = active
        this.lastAccessed = lastAccessed
    }
}
