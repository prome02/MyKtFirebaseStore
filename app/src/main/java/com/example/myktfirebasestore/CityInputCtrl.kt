package com.example.myktfirebasestore

import android.app.ActionBar
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment

//import com.merlin.find_travelmate.ui.main.CitiesDataType

data class CityInputCtrlParas(
    val citiesName: CitiesDataType,
    val citiesNameSearch: CitiesDataType,
    val lv: ListView
)


class CityInputCtrl(var paras: CityInputCtrlParas?) : Fragment() {

    val VAU_KEY_cityname = "edtInput"
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(VAU_KEY_cityname, medtInput.text.toString())
        super.onSaveInstanceState(outState)
    }

    constructor() : this(null) {

    }

    lateinit var medtInput: EditText
    var mlvCitiesname: ListView? = null

    inner class SearchCitiesList : AsyncTask<Any, Int, ArrayList<String>?>() {
        override fun doInBackground(vararg params: Any?): ArrayList<String>? {


            val targetStr = params[0] as String
            val srcStrList = params[1] as CitiesDataType
            val strSearchingList = params[2] as CitiesDataType


            val result = ArrayList<String>()
            val tstr = targetStr.toLowerCase()

            var pos = strSearchingList.binarySearch(tstr)

            if (pos >= 0) {//match case

                return null
            } else {//insert case
                pos = Math.abs(pos + 1)
                val nCou = strSearchingList.count() - 1
                for (i in pos..nCou) {
                    val strCity = strSearchingList[i]

                    if (strCity.length < tstr.length) continue
                    val substr = strCity.substring(0..(tstr.length - 1))

                    if (tstr.compareTo(substr) == 0) {
                        result.add(srcStrList[i])
                    } else break
                }
            }
            return result

        }

        override fun onPostExecute(result: ArrayList<String>?) {

            if (result == null || result.isEmpty()) {
                mlvCitiesname?.visibility = View.GONE
                return
            }

            mlvCitiesname?.adapter = ArrayAdapter<String>(
                this@CityInputCtrl.context!!,
                android.R.layout.simple_list_item_1,
                result
            )
            mlvCitiesname?.bringToFront()
            mlvCitiesname?.visibility = View.VISIBLE
        }
    }

    inner class MyTextWatcher() : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            mlvCitiesname?.bringToFront()
            mlvCitiesname?.visibility = View.GONE
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            var ccp = paras ?: return
            val c1s = ccp.citiesName
            val c2s = ccp.citiesNameSearch

            if (c1s.count() == 0 || s == null || s.isEmpty()) return


            SearchCitiesList().execute(s.toString(), c1s, c2s)


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.city_input_control, container, false)

        mlvCitiesname = ListView(context)
        val disM = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(disM)
        mlvCitiesname?.layoutParams?.width = disM.widthPixels - 20

        medtInput = v.findViewById<EditText>(R.id.edtInput).apply {
            setText(savedInstanceState?.getString(VAU_KEY_cityname))
            addTextChangedListener(MyTextWatcher())
            setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                    if (hasFocus == false)
                        mlvCitiesname?.visibility = View.GONE
                }
            })
        }
//        mlvCitiesname=v.findViewById<ListView>(R.id.lvCitysName).apply {
//
//        }
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun getCityName(): String {
        return medtInput.text.toString()
    }

}