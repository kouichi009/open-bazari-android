package jp.bazari.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import jp.bazari.Apis.IntentKey
import jp.bazari.Apis.SelectBankInterface
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_agyou_list.*
import kotlinx.android.synthetic.main.snippet_simple_backarrow.*


class BanksNameListActivity : AppCompatActivity() {

    val banks = arrayListOf<ArrayList<String>>(
        //あ行
        arrayListOf<String>("愛知銀行", "あおぞら銀行", "青森銀行", "秋田銀行", "足利銀行", "アメリカ銀行", "阿波銀行"),
        //い行
        arrayListOf<String>("イオン銀行", "池田泉州銀行", "伊予銀行", "岩手銀行"),
        //う行
        arrayListOf<String>(
            "上田信用金庫", "魚津市農業協同組合", "魚沼みなみ農業協同組合", "羽後信用金庫",
            "うご農業協同組合", "碓氷安中農業協同組合", "内浦町農業協同組合", "宇都宮農業協同組合", "馬路村農業協同組合",
            "うま農業協同組合", "浦幌町農業協同組合", "ウリ信用組合", "宇和島信用金庫"
        ),
        //え行
        arrayListOf<String>("ＳＭＢＣ信託銀行", "ＳＢＪ銀行", "愛媛銀行"),
        //お行
        arrayListOf<String>("大分銀行", "大垣共立銀行", "沖縄銀行", "沖縄海邦銀行", "オリックス銀行"),
        //か行
        arrayListOf<String>("香川銀行", "鹿児島銀行", "神奈川銀行", "関西アーバン銀行"),
        //き行
        arrayListOf<String>("北九州銀行", "北日本銀行", "紀陽銀行", "京都銀行", "きらぼし銀行", "きらやか銀行", "近畿大阪銀行"),
        //く行
        arrayListOf<String>("熊本銀行", "群馬銀行"),
        //け
        arrayListOf<String>("京葉銀行"),
        //こ
        arrayListOf<String>("高知銀行"),
        //さ
        arrayListOf<String>("西京銀行", "埼玉りそな銀行", "佐賀銀行", "佐賀共栄銀行", "山陰合同銀行"),
        //し
        arrayListOf<String>(
            "滋賀銀行",
            "四国銀行",
            "資産管理サービス信託銀行",
            "静岡銀行",
            "静岡中央銀行",
            "七十七銀行",
            "シティバンク、エヌ・エイ",
            "島根銀行",
            "清水銀行",
            "荘内銀行",
            "新生銀行",
            "親和銀行",
            "ＧＭＯあおぞらネット銀行",
            "ジェーピーモルガン銀行",
            "じぶん銀行",
            "ジャパンネット銀行",
            "十八銀行",
            "十六銀行",
            "常陽銀行"
        ),
        //す
        arrayListOf<String>("住信ＳＢＩネット銀行", "スルガ銀行"),
        //せ
        arrayListOf<String>("セブン銀行", "仙台銀行"),
        //そ
        arrayListOf<String>("ソニー銀行"),
        //た
        arrayListOf<String>("大光銀行", "大正銀行", "但馬銀行", "第三銀行", "第四銀行", "大東銀行", "大和ネクスト銀行"),
        //ち
        arrayListOf<String>("筑邦銀行", "千葉銀行", "千葉興業銀行", "中京銀行", "中国銀行"),
        //つ
        arrayListOf<String>("筑波銀行"),
        //て
        arrayListOf<String>("天塩町農業協同組合", "テラル越前農業協同組合", "天童市農業協同組合", "天白信用農業協同組合"),
        //と
        arrayListOf<String>(
            "東京スター銀行",
            "東邦銀行",
            "東北銀行",
            "東和銀行",
            "徳島銀行",
            "栃木銀行",
            "鳥取銀行",
            "トマト銀行",
            "富山銀行",
            "富山第一銀行",
            "ドイツ銀行"
        ),
        //な
        arrayListOf<String>("長崎銀行", "長野銀行", "名古屋銀行", "南都銀行"),
        //に
        arrayListOf<String>("西日本シティ銀行", "日本トラスティサービス信託銀行", "日本マスタートラスト信託銀行"),
        //ぬ
        arrayListOf<String>("沼津信用金庫"),
        //ね
        arrayListOf<String>("根上農業協同組合"),
        //の
        arrayListOf<String>("野村信託銀行"),
        //は
        arrayListOf<String>("八十二銀行"),
        //ひ
        arrayListOf<String>("東日本銀行", "肥後銀行", "百五銀行", "百十四銀行", "広島銀行"),
        //ふ
        arrayListOf<String>("福井銀行", "福岡銀行", "福岡中央銀行", "福島銀行", "福邦銀行"),
        //へ
        arrayListOf<String>("碧海信用金庫", "べっぷ日出農業協同組合"),
        //ほ
        arrayListOf<String>("豊和銀行", "北越銀行", "北都銀行", "北洋銀行", "北陸銀行", "北海道銀行", "北國銀行", "香港上海銀行"),
        //ま
        arrayListOf<String>("毎日信用組合"),
        //み
        arrayListOf<String>(
            "三重銀行",
            "みずほ銀行",
            "みずほ信託銀行",
            "みちのく銀行",
            "三井住友銀行",
            "三井住友信託銀行",
            "三菱ＵＦＪ信託銀行",
            "三菱ＵＦＪ銀行",
            "みなと銀行",
            "南日本銀行",
            "宮崎銀行",
            "宮崎太陽銀行"
        ),
        //む
        arrayListOf<String>("武蔵野銀行"),
        //め
        arrayListOf<String>("めぐみの農業協同組合", "目黒信用金庫", "女満別町農業協同組合", "芽室町農業協同組合"),
        //も
        arrayListOf<String>("もみじ銀行"),
        //や
        arrayListOf<String>("山形銀行", "山口銀行", "山梨中央銀行"),
        //ゆ
        arrayListOf<String>("ゆうちょ銀行"),
        //よ
        arrayListOf<String>("横浜銀行"),
        //ら
        arrayListOf<String>("楽天銀行"),
        //り
        arrayListOf<String>("りそな銀行", "琉球銀行"),
        //る
        arrayListOf<String>("留萌信用金庫"),
        //れ
        arrayListOf<String>("苓北町農業協同組合"),
        //ろ
        arrayListOf<String>("労働金庫連合会"),
        //わ
        arrayListOf<String>("若狭農業協同組合", "和歌山県医師信用組合", "和歌山県信用農業協同組合連合会", "わかやま農業協同組合", "稚内信用金庫", "稚内農業協同組合")

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banks_name_list)

        val bundle = intent.extras
        val str = bundle.getString(IntentKey.GOJUON2.name)

        backArrow.setOnClickListener {
            finish()
        }

        lateinit var selectArray: ArrayList<String>
        when (str) {

            "あ" -> {
                selectArray = banks[0]
            }

            "い" -> {
                selectArray = banks[1]
            }
            "う" -> {
                selectArray = banks[2]

            }
            "え" -> {
                selectArray = banks[3]
            }
            "お" -> {
                selectArray = banks[4]

            }
            "か" -> {
                selectArray = banks[5]

            }
            "き" -> {
                selectArray = banks[6]

            }
            "く" -> {
                selectArray = banks[7]

            }
            "け" -> {
                selectArray = banks[8]

            }
            "こ" -> {
                selectArray = banks[9]

            }
            "さ" -> {
                selectArray = banks[10]

            }
            "し" -> {
                selectArray = banks[11]

            }
            "す" -> {
                selectArray = banks[12]

            }
            "せ" -> {
                selectArray = banks[13]

            }
            "そ" -> {
                selectArray = banks[14]

            }
            "た" -> {
                selectArray = banks[15]

            }
            "ち" -> {
                selectArray = banks[16]

            }
            "つ" -> {
                selectArray = banks[17]

            }
            "て" -> {
                selectArray = banks[18]

            }
            "と" -> {
                selectArray = banks[19]

            }
            "な" -> {
                selectArray = banks[20]

            }
            "に" -> {
                selectArray = banks[21]
            }
            "ぬ" -> {
                selectArray = banks[22]
            }
            "ね" -> {
                selectArray = banks[23]
            }
            "の" -> {
                selectArray = banks[24]

            }
            "は" -> {
                selectArray = banks[25]

            }
            "ひ" -> {
                selectArray = banks[26]

            }
            "ふ" -> {
                selectArray = banks[27]

            }
            "へ" -> {
                selectArray = banks[28]

            }
            "ほ" -> {
                selectArray = banks[29]

            }
            "ま" -> {
                selectArray = banks[30]

            }
            "み" -> {
                selectArray = banks[31]

            }
            "む" -> {
                selectArray = banks[32]

            }
            "め" -> {
                selectArray = banks[33]

            }
            "も" -> {
                selectArray = banks[34]

            }
            "や" -> {
                selectArray = banks[35]

            }
            "ゆ" -> {
                selectArray = banks[36]

            }
            "よ" -> {
                selectArray = banks[37]

            }
            "ら" -> {
                selectArray = banks[38]

            }
            "り" -> {
                selectArray = banks[39]

            }
            "る" -> {
                selectArray = banks[40]

            }
            "れ" -> {
                selectArray = banks[41]

            }
            "ろ" -> {
                selectArray = banks[42]

            }
            "わ" -> {
                selectArray = banks[43]

            }

        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, selectArray)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->

            val intent = Intent(this@BanksNameListActivity, BankSelectActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            var selectBankInterface: SelectBankInterface? = null
            selectBankInterface = bankSelectActivity
            selectBankInterface?.selectBank(selectArray[position])
            startActivity(intent)
        }
    }
}
