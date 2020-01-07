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
import com.google.firebase.firestore.ktx.toObject
import com.google.protobuf.LazyStringArrayList
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main2.*
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


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
                when (msg.what) {
                    GetCities.TagNum -> {
                        Log.d(GetCities.Tag, "TestTask.arg1=${msg.arg1}")

                        val list = msg.data.getParcelable<CitiesDataType>(GetCities.Tag)

                        val act = mOuter?.get()
                        val lv = act?.findViewById<ListView>(R.id.lvCities)
                        if (act == null || list == null) return

                        lv?.adapter = ArrayAdapter<String>(
                            act,
                            android.R.layout.simple_list_item_1,
                            list
                        )

                    }
                }
                super.handleMessage(msg)
            }
        }

//        class GetSearingList(val mHdle: Handler, val cities:CitiesDataType, val strSrc:String) : Runnable {
//            override fun run() {
//
//
//            }
//
//            companion object {
//                val TagNum = 12
//                val Tag = GetSearingList.javaClass.simpleName
//            }
//        }

        class GetCities(val mHdle: Handler) : Runnable {
            override fun run() {
                val db = FirebaseFirestore.getInstance()
                val str = "cities_en"
                db.collection("cities").document(str).get()
                    .addOnSuccessListener { doc ->

                        if (doc != null) {

//                            val ma = doc.data as HashMap<String, List<String?>>

                            val ma = doc.getData()

                            if (ma == null) throw Exception("cities get error 01")
                            val lst = ma[str]
                            if (lst == null) throw Exception("cities get error 02")

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

            }

            companion object {
                val TagNum = 16
                val Tag = GetCities.javaClass.simpleName
            }
        }
    }

    val mHdle = MyHandler(this)


    lateinit var mThread: Thread
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        mThread = Thread(GetCities(MyHandler(this)))
        mThread.start()


        edtInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }


    val email = "asx@gtc.com.tw"
    val collName = "users"
    val TAG = "err"
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
}
