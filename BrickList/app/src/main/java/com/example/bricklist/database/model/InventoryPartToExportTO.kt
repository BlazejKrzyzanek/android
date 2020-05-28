package com.example.bricklist.database.model

class InventoryPartToExportTO {
    var itemType: String? = null
    var itemId: Int = 0
    var color: Int = 0
    var qtyFilled: Int = 0
    var condition: String? = null

    constructor()

    constructor(itemType: String?, itemId: Int, color: Int, qtyFilled: Int, condition: String?) {
        this.itemType = itemType
        this.itemId = itemId
        this.color = color
        this.qtyFilled = qtyFilled
        this.condition = condition
    }

    constructor(itemType: String?, itemId: Int, color: Int, qtyFilled: Int) {
        this.itemType = itemType
        this.itemId = itemId
        this.color = color
        this.qtyFilled = qtyFilled
    }


}