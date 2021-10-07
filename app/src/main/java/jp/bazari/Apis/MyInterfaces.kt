package jp.bazari.Apis

interface SelectBankInterface {

    fun selectBank(bank: String)
}

//interface SelectCategoriesInterface {
//
//    fun selectCategories(category1: String, category2: String)
//}


interface PurchaseActivityInterface {

    fun changePurchaseBtn()
}

interface ProfileEditActivityInterface {

    fun changeNavProfileIv()
}

interface NotificationActivityInterface {

    fun updateNotificationAC()
}

interface PurchaseListActivityInterface {

    fun updatePurchaseListAC()
}

interface SellListActivityInterface {

    fun updateSellListAC()
}