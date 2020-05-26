package com.example.bricklist.database.model

class PartTO {
    var id: Int = 0
    var typeId: Int = 0
    var code: String? = null
    var name: String? = null
    var namePL: String? = null
    var categoryId: Int = 0

    constructor()

    constructor(
        id: Int,
        typeId: Int,
        code: String?,
        name: String?,
        namePL: String?,
        categoryId: Int
    ) {
        this.id = id
        this.typeId = typeId
        this.code = code
        this.name = name
        this.namePL = namePL
        this.categoryId = categoryId
    }
}