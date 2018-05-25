package com.dog_roulette.dogroulette

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val m = supportFragmentManager
        m.beginTransaction().add(R.id.main_act, PlayerFragment()).commit()

    }

}
