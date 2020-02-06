package com.example.myktfirebasestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.browser_user_info.*

class BrowserUserAct : AppCompatActivity() {

    companion object {
//        val sBundle = "BrowserUserAct"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.browser_user_info)

        (frag_gen_sel as GenSelectFrag).apply {
            setEnable(false)
        }

        val bu = intent.getBundleExtra(WrapTravelDatas::class.java.simpleName)
        val obj = bu?.getParcelable<WrapTravelDatas>(WrapTravelDatas::class.java.simpleName)
        if (obj != null) {
            val user = obj.userinfo
            user ?: return

            val txt2 = findViewById<TextView>(R.id.txt_desp)
            val txt1 = findViewById<TextView>(R.id.txt_user_content)
            user.outputContent(this@BrowserUserAct, txt1, txt2)

            val frg = supportFragmentManager.findFragmentById(R.id.frag_gen_sel)
            (frg as GenSelectFrag).apply {
                setState(user.gender)
            }


            val lv = findViewById<ListView>(R.id.lvTrips)
            if (lv != null) {
                val lst = ArrayList<String>()

                for (it in obj.list) {
                    lst.add(it.makeContentTitle())
                }
                lv.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lst)
            }
        }
    }
}
