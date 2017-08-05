package com.syjgin.pathfinderfeats.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.syjgin.pathfinderfeats.Models.Feat
import com.syjgin.pathfinderfeats.PersistentData.MainDatabaseSource
import com.syjgin.pathfinderfeats.R
import io.requery.meta.EntityModelBuilder
import io.requery.query.Result

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
