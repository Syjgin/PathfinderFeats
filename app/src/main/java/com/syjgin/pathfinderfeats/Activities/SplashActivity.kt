package com.syjgin.pathfinderfeats

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import io.requery.meta.EntityModelBuilder

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initDatabaseIfNeeded()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun initDatabaseIfNeeded() {
        val model = EntityModelBuilder("main").build()
        MainDatabaseSource(this, model, 1)
    }
}
