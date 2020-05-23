package com.example.bricklist.database

import android.provider.BaseColumns

object BrickListContract {
    object Categories : BaseColumns {
        const val TABLE_NAME = "Categories"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_CODE = "Code"
        const val COLUMN_NAME_NAME = "Name"
        const val COLUMN_NAME_NAME_PL = "NamePL"
    }

    object Codes : BaseColumns {
        const val TABLE_NAME = "Codes"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_ITEM_ID = "ItemID"
        const val COLUMN_NAME_COLOR_ID = "ColorID"
        const val COLUMN_NAME_CODE = "Code"
        const val COLUMN_NAME_IMAGE = "Image"
    }

    object Colors : BaseColumns {
        const val TABLE_NAME = "Colors"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_CODE = "Code"
        const val COLUMN_NAME_NAME = "Name"
        const val COLUMN_NAME_NAME_PL = "NamePL"
    }

    object Inventories : BaseColumns {
        const val TABLE_NAME = "Inventories"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_NAME = "Name"
        const val COLUMN_NAME_ACTIVE = "Active"
        const val COLUMN_NAME_LAST_ACCESSED = "LastAccessed"
    }

    object InventoriesParts : BaseColumns {
        const val TABLE_NAME = "InventoriesParts"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_INVENTORY_ID = "InventoryID"
        const val COLUMN_NAME_TYPE_ID = "TypeID"
        const val COLUMN_NAME_ITEM_ID = "ItemID"
        const val COLUMN_NAME_QUANTITY_IN_SET = "QuantityInSet"
        const val COLUMN_NAME_QUANTITY_IN_STORE = "QuantityInStore"
        const val COLUMN_NAME_COLOR_ID = "ColorID"
        const val COLUMN_NAME_EXTRA = "Extra"
    }

    object ItemTypes : BaseColumns {
        const val TABLE_NAME = "ItemTypes"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_CODE = "Code"
        const val COLUMN_NAME_NAME = "Name"
        const val COLUMN_NAME_NAME_PL = "NamePL"
    }

    object Parts : BaseColumns {
        const val TABLE_NAME = "Parts"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_TYPE_ID = "TypeID"
        const val COLUMN_NAME_CODE = "Code"
        const val COLUMN_NAME_NAME = "Name"
        const val COLUMN_NAME_NAME_PL = "NamePL"
        const val COLUMN_NAME_Category_ID = "CategoryID"
    }

}