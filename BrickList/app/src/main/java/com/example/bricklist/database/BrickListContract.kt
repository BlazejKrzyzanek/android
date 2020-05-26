package com.example.bricklist.database

import android.provider.BaseColumns

object BrickListContract {
    object Categories : BaseColumns {
        const val TABLE_NAME = "Categories"
        const val ID = "id"
        const val CODE = "Code"
        const val NAME = "Name"
        const val NAME_PL = "NamePL"
    }

    object Codes : BaseColumns {
        const val TABLE_NAME = "Codes"
        const val ID = "id"
        const val ITEM_ID = "ItemID"
        const val COLOR_ID = "ColorID"
        const val CODE = "Code"
        const val IMAGE = "Image"
    }

    object Colors : BaseColumns {
        const val TABLE_NAME = "Colors"
        const val ID = "id"
        const val CODE = "Code"
        const val NAME = "Name"
        const val NAME_PL = "NamePL"
    }

    object Inventories : BaseColumns {
        const val TABLE_NAME = "Inventories"
        const val ID = "id"
        const val NAME = "Name"
        const val ACTIVE = "Active"
        const val LAST_ACCESSED = "LastAccessed"
    }

    object InventoriesParts : BaseColumns {
        const val TABLE_NAME = "InventoriesParts"
        const val ID = "id"
        const val INVENTORY_ID = "InventoryID"
        const val TYPE_ID = "TypeID"
        const val ITEM_ID = "ItemID"
        const val QUANTITY_IN_SET = "QuantityInSet"
        const val QUANTITY_IN_STORE = "QuantityInStore"
        const val COLOR_ID = "ColorID"
        const val EXTRA = "Extra"
    }

    object ItemTypes : BaseColumns {
        const val TABLE_NAME = "ItemTypes"
        const val ID = "id"
        const val CODE = "Code"
        const val NAME = "Name"
        const val NAME_PL = "NamePL"
    }

    object Parts : BaseColumns {
        const val TABLE_NAME = "Parts"
        const val ID = "id"
        const val TYPE_ID = "TypeID"
        const val CODE = "Code"
        const val NAME = "Name"
        const val NAME_PL = "NamePL"
        const val CATEGORY_ID = "CategoryID"
    }

}