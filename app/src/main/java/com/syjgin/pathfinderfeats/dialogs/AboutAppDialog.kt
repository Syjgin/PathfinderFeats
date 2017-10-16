package com.syjgin.pathfinderfeats.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.activities.MainActivity

/**
 * Created by user1 on 31.08.17.
 */
class AboutAppDialog(context: Context) : AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_dialog)
        val textView = findViewById<TextView>(R.id.textView)
        textView.setText(Html.fromHtml(MainActivity.ABOUT_TEXT))
        textView.setMovementMethod(LinkMovementMethod.getInstance())
        textView.linksClickable = true
        textView.isClickable = true
        setTitle(R.id.info)
        val close = findViewById<Button>(R.id.close)
        close.setOnClickListener({view ->
            dismiss()
        })
    }
}