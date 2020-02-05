package com.example.myktfirebasestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import kotlinx.android.synthetic.main.browser_user_info.*

class BrowserUserAct : AppCompatActivity() {

    companion object {
        val sBundle = "BrowserUserAct"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.browser_user_info)

        (frag_gen_sel as GenSelectFrag).apply {
            setEnable(false)
        }

        val bu = intent.getBundleExtra(sBundle)
        val obj = bu?.getParcelable<WrapTravelDatas>(WrapTravelDatas::class.java.simpleName)
        if (obj != null) {
            val user = obj.userinfo
            user ?: return
            edtUsername.setText(user.name)

            findViewById<Button>(R.id.btn_select_birthday).apply {
                text = HelpObject.dateToString(user.birthday)
            }

            findViewById<EditText>(R.id.edtLanguage).apply {
                setText(user.language)
            }

            findViewById<EditText>(R.id.edtCountry).apply {
                setText(user.country)
            }

            val frg = supportFragmentManager.findFragmentById(R.id.frag_gen_sel)
            (frg as GenSelectFrag).apply {
                setState(user.gender)
            }

            findViewById<EditText>(R.id.edtDesc).apply {
                setText(user.desp)
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
