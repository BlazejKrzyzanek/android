package com.example.bricklist.database.model

class InventoryPartTO {
    var id: Int = 0
    var inventoryId: Int = 0
    var typeId: Int = 0
    var itemId: Int = 0
    var quantityInSet: Int = 0
    var quantityInStore: Int = 0
    var colorId: Int = 0
    var extra: Int = 0

    constructor(
        id: Int,
        inventoryId: Int,
        typeId: Int,
        itemId: Int,
        quantityInSet: Int,
        quantityInStore: Int,
        colorId: Int,
        extra: Int
    ) {
        this.id = id
        this.inventoryId = inventoryId
        this.typeId = typeId
        this.itemId = itemId
        this.quantityInSet = quantityInSet
        this.quantityInStore = quantityInStore
        this.colorId = colorId
        this.extra = extra
    }

    constructor()
}