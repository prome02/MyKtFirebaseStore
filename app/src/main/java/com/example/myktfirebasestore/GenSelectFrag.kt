package com.example.myktfirebasestore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import java.lang.Exception

class GenSelectFrag: Fragment() {
    override fun onPause() {
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


//    val tbList=ArrayList<ToggleButton>(8)
//    lateinit var tbM:ToggleButton
//    lateinit var tbF:ToggleButton
//    lateinit var tbL:ToggleButton
//    lateinit var tbG:ToggleButton
//    lateinit var tbB:ToggleButton
//    lateinit var tbQ:ToggleButton
//    lateinit var tbT:ToggleButton
//    lateinit var tbO:ToggleButton
//
//    val checkListner=object :CompoundButton.OnCheckedChangeListener{
//        override fun onCheckedChanged(btn: CompoundButton?, isChecked: Boolean) {
//
//            try {
//                when( btn?.id) {
//                    R.id.tbtn_gen_sel_other-> {
//                        if (isChecked) {
//
//                            for (it in tbList) {
//                                it.isChecked = false
//
//                            }
//                        }
//                    }
//                    R.id.tbtn_gen_sel_male-> if(tbF.isChecked)tbF.isChecked=false
//                    R.id.tbtn_gen_sel_female->if(tbM.isChecked)tbM.isChecked=false
//                    else-> {}
//
//                }
//            } catch (e: Exception) {
//            }
//        }
//    }

    lateinit var radioGroup: RadioGroup
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
//        val ids= listOf<Int>( R.id.tbtn_gen_sel_bi, R.id.tbtn_gen_sel_tom,
//            R.id.tbtn_gen_sel_male,R.id.tbtn_gen_sel_female,R.id.tbtn_gen_sel_lesbian,
//            R.id.tbtn_gen_sel_gay,R.id.tbtn_gen_sel_queer)
//
//        for(it in ids) tbList.add(mainV.findViewById<ToggleButton>(it))
//
//
//        tbB=mainV.findViewById<ToggleButton>(R.id.tbtn_gen_sel_bi)
//        tbM=mainV.findViewById<ToggleButton>(R.id.tbtn_gen_sel_male)
//        tbF=mainV.findViewById<ToggleButton>(R.id.tbtn_gen_sel_female)
//        tbL=mainV.findViewById<ToggleButton>(R.id.tbtn_gen_sel_lesbian)
//        tbG=mainV.findViewById<ToggleButton>(R.id.tbtn_gen_sel_gay)
//        tbQ=mainV.findViewById<ToggleButton>(R.id.tbtn_gen_sel_queer)
//        tbT=mainV.findViewById<ToggleButton>(R.id.tbtn_gen_sel_tom)
//
//        tbO=mainV.findViewById<ToggleButton>(R.id.tbtn_gen_sel_other)
//        if(tbO!=null) tbO.setOnCheckedChangeListener(checkListner)
//
//        for (it in tbList) it.setOnCheckedChangeListener(checkListner)
        return mainV
    }


}