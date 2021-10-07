package jp.bazari.Apis

enum class DBKey {
    posts,
    myPosts,
    myPurchasePosts,
    mySellPosts,
    comments,
    chats,
    value,
    myValue,
    myCharge,
    mySoldOutPosts,
    soldOutPosts,
    users,
    myCommentingPosts,
    myLikingPosts,
    notification,
    myNotification,
    existNotification,
    myBank,
    bankTransferDates,
    address,
    complains,
    cards,
    brandCandidates

}

enum class TransactionStatus {
    sell,
    transaction
}

enum class LoginType {
    Email,
    Facebook
}

enum class Type {
    naviPurchase, naviShip, naviEvaluatePurchaser, naviEvaluateSeller, naviMessage
}

enum class Value {
    sun, cloud, rain
}

enum class CardType {
    visa, mastercard
}

enum class IntentKey {
    POST_ID, UID, POSTS, COMMENTS, CHATS, NOTIFICATIONS, USERMODELS, POST, USERMODEL, GOJUON1, GOJUON2, BANKNAME, TOTALAMOUNT, PREFECTURE, VALUES,
    INDEX_ROW, CARDS, WHICHACTIVITYTODISTINGUISH, CAPTION, CATEGORY, CATEGORY_INDEX, BRAND, PRODUCT_STATUS, SHIPPAYER, SHIPPAYERSELECTED
    ,SHIPINFOSELECTION, SHIPINFO, USERID, FROMACTIVITY
}


//PostActivity
val IMAGE_PICK_CODE = 1
val CAPTION_CODE = 2
val CATEGORY_CODE = 3
val BRAND_CODE = 4
val PRODUCT_STATUS_CODE = 5
val SHIPPAYER_CODE = 6
val SHIPWAY_CODE = 7
val SHIPDEADLINE_CODE = 8
val PREFECTURE_CODE = 9
val CAMERA_PERMISSION_CODE = 10
val ALBUM_PERMISSION_CODE = 11
val CAMERA_PICK_CODE = 12

val SELF_INTRODUCTION = "SELF_INTRODUCTION"

var herokuForStripeServerUrl = ""

var email_inquiry = "appruby1192@gmail.com"

val ratioWidthKey = "ratioWidthKey"
val ratioHeightKey = "ratioHeightKey"

//Repro token
val reproToken = ""

//need to change
val SIREN_JSON_URL = ""

// 買った側
val waitForShip = "商品発送待ち"
val catchProduct = "商品の受取"
val waitForValue = "相手の評価待ち"
val buyFinish = "購入済"



// 売った側
var nowOnSell = "出品中"
var ship = "商品の発送"
var waitCatch = "受取確認待ち"
var valueBuyer = "購入者の評価"
var soldFinish = "売却済"

var shipPayerList = arrayListOf<String>("送料込み (あなたが負担)","着払い (購入者が負担)")
var shipDatesList = arrayListOf<String>("支払い後、1〜2日で発送","支払い後、2〜3日で発送","支払い後、4〜7日で発送")

val categories = arrayListOf<String>("デジタルカメラ", "フィルムカメラ","レンズ (オートフォーカス)","レンズ (マニュアルフォーカス)", "ミラーレス一眼","コンパクトデジタルカメラ","ビデオカメラ","ケース/バッグ","ストロボ/照明","フィルター","防湿庫","露出計","暗室関連用品","その他")

val productStatuses = arrayListOf<String>("新品・未使用","AA (新品同様)","A (美品)","AB (良品)","B (並品)","C (やや難あり)", "J (故障品・ジャンク品)")
val productSubtitles = arrayListOf<String>("新品のもの","外観に使用した形跡が無い。非常にきれいで、新品同様のもの",
"外観に使用した形跡が少ない。きれいで、正常作動するもの", "外観に多少のキズや擦れなどがある。正常作動するもの",
"外観に比較的目立つキズや擦れがある。テカリなどがあるが、正常作動するもの", "外観に目立つキズや擦れが多い。テカリなどがあるが、作動するもの",
"作動に問題のあるもの。部品取り、コレクション用に。破損品。")

var pageDefault = 24
var increaseNumDefault = 24

// 3%
var commisionRate: Double = 0.03

var pushNotiTitleCount = 30

var minimumPrice = 300
var maximumPrice = 200000
var minimumTransferApplyPrice = 1000
var maximumTransferApplyPrice = 500000

var StripeApiKey = ""
// stripe secret key // サーバー側のキー
var StripeSecretKeyForServer = ""


var firebaseServerKey = ""

var anonymousImageURL = ""
var adminImageURL = ""