package com.example.myktfirebasestore

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.protobuf.compiler.PluginProtos
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.*
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


data class User2(
    var id: String = "", var name: String = "", var birthday: Date = Date(),
    var gender: Int = 0, var language: String = "",
    var country: String = "", var desp: String = ""
)

@Parcelize
class CitiesDataType() : ArrayList<String>(), Parcelable


class Main2Activity : AppCompatActivity() {

    companion object {


        class MyHandler : Handler {
            var mOuter: WeakReference<Main2Activity>? = null

            constructor(ref: Main2Activity) {
                mOuter = WeakReference<Main2Activity>(ref)
            }

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    GetCities.TagNum -> {
                        Log.d(GetCities.Tag, "TestTask.arg1=${msg.arg1}")

                        val act = mOuter?.get()
                        val list = msg.data.getParcelable<CitiesDataType>(GetCities.Tag)
                        if (list != null && act != null) {

                            list.sort()
                            act.cities = list
                            val listSearched = act.citiesSearing
                            for (it in list) listSearched.add(it.toLowerCase())

                            for (i in 0..30) println("$i) ${listSearched[i]}")
                        }
//
//                        val lv = act?.findViewById<ListView>(R.id.lvCities)
//                        if (act == null || list == null) return
//
//                        lv?.adapter = ArrayAdapter<String>(
//                            act,
//                            android.R.layout.simple_list_item_1,
//                            list
//                        )

                    }
                    SearchingTask.TagNum -> {
                        val act = mOuter?.get()
                        val lv = act?.findViewById<ListView>(R.id.lvCities)
                        val list = msg.data.getParcelable<CitiesDataType>(GetCities.Tag)
                        if (act == null || list == null) return
                        lv?.adapter =
                            ArrayAdapter<String>(act, android.R.layout.simple_list_item_1, list)
                        lv?.visibility = View.VISIBLE
                    }
                    R.id.A1 -> {
                        val res = msg.data.getString(R.id.A1.toString())
                        Log.d("TAG", res)
                    }
                }

            }
        }

        class SearchingTask(
            val mHdle: Handler,
            val mSrcStrList: CitiesDataType,
            val mStrSearchingList: CitiesDataType,
            val targetStr: String
        ) : Runnable {
            override fun run() {
                var tstr = targetStr.toLowerCase()


                var pos = mStrSearchingList.binarySearch(tstr)
                val result = CitiesDataType()
                if (pos >= 0) {//match case
                    result.add(mSrcStrList[pos])

                } else {//insert case
                    pos = Math.abs(pos + 1)
                    for (i in pos..(mStrSearchingList.count() - 1)) {
                        val strCity = mStrSearchingList[i]

                        if (strCity.length < tstr.length) continue
                        val substr = strCity.substring(0..(tstr.length - 1))

                        if (tstr.compareTo(substr) == 0) {
                            result.add(mSrcStrList[i])
                        } else break
                    }
                }

                val msg = mHdle.obtainMessage()
                val bdl = Bundle().apply {
                    putParcelable(Tag, result)
//                    putStringArrayList(SearchingTask.Tag, result)
                }
                msg.what = TagNum
                msg.data = bdl
                mHdle.sendMessage(msg)
            }

            companion object {
                val TagNum = 12
                val Tag = SearchingTask.javaClass.simpleName
            }
        }


        class GetCities(val mHdle: Handler) : Runnable {
            override fun run() {
                val db = FirebaseFirestore.getInstance()
                val str = "cities_en"
                db.collection("cities").document(str).get()
                    .addOnSuccessListener { doc ->
                            val ma = doc.getData()
                        val lst = ma?.get(str)
                            if (lst is List<*> && lst.count() > 0) {
                                val ary = CitiesDataType()
                                for (it in lst) {
                                    if (it is String) ary.add(it)
                                }

                                val msg = mHdle.obtainMessage()
                                Log.d(Tag, "TestTask.what=${msg.what}")
                                msg.what = TagNum
                                msg.data = Bundle().apply {
                                    putParcelable(Tag, ary)
//                                    putStringArrayList(Tag, ary)
                                }
                                mHdle.sendMessage(msg)
                            }
                    }
            }

            companion object {
                val TagNum = 16
                val Tag = GetCities.javaClass.simpleName
            }
        }
    }

    val mHdle = MyHandler(this)

    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)






        listView = findViewById<ListView>(R.id.lvCities)
        Thread(GetCities(MyHandler(this))).start()



        edtInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                listView.visibility = View.INVISIBLE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (cities.count() == 0 || s == null || s.length == 0) return
                Thread(
                    SearchingTask(
                        MyHandler(this@Main2Activity),
                        cities,
                        citiesSearing,
                        s?.toString()
                    )
                ).start()
            }
        })
    }


    val email = "asx@gtc.com.tw"
    val collName = "users"
    val TAG = "err"
    var cities = CitiesDataType()
    var citiesSearing = CitiesDataType()
    fun onGetDataFromFF(v: View) {

        val db = FirebaseFirestore.getInstance()
        db.collection(collName).document(email).get()
            .addOnSuccessListener {
                try {

                    if (it != null) {
                        val user = it.toObject(User2::class.java)
                        Log.d(TAG, "name=${user?.name}")
                    }
                } catch (ex: Exception) {
                    Log.d(TAG, ex.message)
                }

            }.addOnFailureListener {
                Log.d(TAG, " process failure!")
            }
    }

    fun onAddDataToFF(v: View) {
        val date = HelpObject.stringToDate(this, "1971/03/04")
        val info = User2(email, "Mary", date, 2, "chinese", "taiwan", "")
        val db = FirebaseFirestore.getInstance()
        db.collection(collName).document(info.id).set(info)
            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                override fun onComplete(p0: Task<Void?>) {
                    if (p0.isSuccessful()) {
                        Log.d(TAG, "upload ok !")
                    }
                }

            })
    }


    fun onBinarySearch(v: View) {
//        val ary=resources.getStringArray(R.array.cities_en)
//
//        class CitiesComp:Comparator<String>{
//            override fun compare(o1: String?, o2: String?): Int {
//
//                    if(o1==null || o2==null) throw Exception("string null")
//                    return o1?.compareTo(o2)
//
//            }
//        }
//
//        ary.sort()
//        val pos=ary.binarySearch("Taipei")
//
//
//        for (i in 0..5)
//        {
//            Log.d(TAG," ${ary[pos+i]}")
//        }
    }

    fun onClickReadWriteTest(v: View) {
        try {
            val str = "test.txt"
            val dir = this.filesDir
            val pa = dir.path
//            val f=File(pa,str)
//            f.createNewFile()
//            val osw=OutputStreamWriter(FileOutputStream(f))
//            val sp=System.lineSeparator()
//            for(it in cities)
//            {
//                osw.write(it+sp)
//            }
//            osw.close()

            val isr = InputStreamReader(FileInputStream(File(pa, str)))
            val ls = isr.readLines()
            for (it in ls) {
                println(it)
            }
            isr.close()
        } catch (e: Exception) {
            Log.d("er", e.message)
        }

    }

    fun onCurrentDate(v: View) {
        val cal = Calendar.getInstance()
        Log.d("msg", "${Calendar.getInstance().time.toString()}")
    }

    fun onPathFF(v: View) {
        Thread(A1(mHdle)).start()
    }


}
