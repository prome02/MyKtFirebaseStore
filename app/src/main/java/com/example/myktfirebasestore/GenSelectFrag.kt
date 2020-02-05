package com.example.myktfirebasestore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment

class GenSelectFrag: Fragment() {
    override fun onPause() {
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    val map = hashMapOf<Gender, Int>(
        Gender.M to R.id.tbtn_gen_sel_male,
        Gender.F to R.id.tbtn_gen_sel_female,
        Gender.L to R.id.tbtn_gen_sel_lesbian,
        Gender.G to R.id.tbtn_gen_sel_gay,
        Gender.T to R.id.tbtn_gen_sel_tom,
        Gender.Q to R.id.tbtn_gen_sel_queer,
        Gender.B to R.id.tbtn_gen_sel_bi,
        Gender.Other to R.id.tbtn_gen_sel_other
    )
//    val ids= listOf<Int>(R.id.tbtn_gen_sel_male,
//    R.id.tbtn_gen_sel_female,
//    R.id.tbtn_gen_sel_lesbian,
//    R.id.tbtn_gen_sel_gay,
//    R.id.tbtn_gen_sel_tom,
//    R.id.tbtn_gen_sel_queer,
//    R.id.tbtn_gen_sel_bi,
//    R.id.tbtn_gen_sel_other)

    lateinit var radioGroup: RadioGroup
    fun setState(gr: Gender) {

        if (gr == Gender.Unknown) return
        val id = map[gr]
        radioGroup.check(id!!)

    }

    fun getState(): Gender {
        when (radioGroup.checkedRadioButtonId) {
            R.id.tbtn_gen_sel_male -> return Gender.M
            R.id.tbtn_gen_sel_female -> return Gender.F
            R.id.tbtn_gen_sel_lesbian -> return Gender.L
            R.id.tbtn_gen_sel_gay -> return Gender.G
            R.id.tbtn_gen_sel_tom -> return Gender.T
            R.id.tbtn_gen_sel_queer -> return Gender.Q
            R.id.tbtn_gen_sel_bi -> return Gender.B
            else -> return Gender.Other
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainV = inflater.inflate(R.layout.gender_selection, container, false)
        radioGroup = mainV.findViewById<RadioGroup>(R.id.rg_gender)
        return mainV
    }

    fun setEnable(bEnable: Boolean) {
        radioGroup.isEnabled = bEnable
    }
}