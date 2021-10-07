package jp.bazari.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.bazari.Apis.IntentKey
import jp.bazari.R
import kotlinx.android.synthetic.main.activity_caption_edit.*
import kotlinx.android.synthetic.main.snippet_top_save.*


class CaptionEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caption_edit)

        val bundle = intent.extras
        val preCaption = bundle.getString(IntentKey.CAPTION.name)

        captionEdit.setText(preCaption)

        saveBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra(IntentKey.CAPTION.name, captionEdit.text.toString().trim())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
