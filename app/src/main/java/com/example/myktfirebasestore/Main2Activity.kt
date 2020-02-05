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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.*
import java.lang.Exception
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

@Parcelize
open class TravelData(
//variable name can not be changed,
// because it's related with name of the querying field in Firebase Firestore
    var cityFrom: String = "",
    var cityTo: String = "",
    var id: Long = -1,
    var transpotation: Int = -1,
    var transInfo: String = "",
    var dDepart: Date = Date(),
    var dArrive: Date = Date(),
    var description: String = "",
    var emailID: String = ""
) : Parcelable {
    constructor(dd: String, da: String, from: String, to: String, formattedStr: String) : this(
        from,
        to
    ) {

        setDateWithString(dd, da, formattedStr)
    }

//    fun isTheSame(da: TravelData): Boolean {
//        if(da.dArrive.compareTo(dArrive)!=0 ||
//                da.dDepart.compareTo(dDepart)!=0 ||
//                da.) return false
//        return true
//    }

    fun areContentsTheSame(da: TravelData): Boolean {

        if (dDepart.compareTo(da.dDepart) != 0) return false
        if (dArrive.compareTo(da.dArrive) != 0) return false
        if (cityFrom != da.cityFrom) return false
        if (cityTo != da.cityTo) return false
//         if(id!=da.id) return false
        return true
    }

    fun setDateWithString(dDStr: String, dAStr: String, formattedStr: String) {

        try {
            java.text.SimpleDateFormat(formattedStr).apply {
                dDepart = parse(dDStr)
                dArrive = parse(dAStr)
            }
        } catch (e: Exception) {
            Log.d("tda", e.message.toString())
        }
    }

    fun isIntersectedInDate(data: TravelData): Boolean {
        if (data.dDepart >= dArrive) return false
        else if (data.dArrive <= dDepart) return false
        else return true
    }

    fun makeContentTitle(): String {
        val dft = java.text.SimpleDateFormat("MMM dd")
        val strDp = dft.format(dDepart)
        val strAr = dft.format(dArrive)
        return "$strDp -> $strAr, from:$cityFrom   to:$cityTo"
    }
}
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
                            val inputFrag =
                                act.supportFragmentManager.findFragmentById(R.id.input_frag) as CityInputCtrl
                            inputFrag.paras = CityInputCtrlParas(list, listSearched, ListView(act))

//                            for (i in 0..30) println("$i) ${listSearched[i]}")
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

    //    lateinit var inputFrag:CityInputCtrl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


//        inputFrag.ctx=this.

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
        val date = HelpObject.stringToDate("1971/03/04")
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

    lateinit var a1: A1
    fun onPathFF(v: View) {
        a1 = A1(mHdle)
        Thread(a1).start()
    }


    fun onUpdate(v: View) {
        val db = FirebaseFirestore.getInstance()
        val pref = db.collection("trips").whereEqualTo("A1", "are you ok").get()
            .addOnSuccessListener {
                for (doc in it.documents) {
                    val targetDoc = db.document(doc.reference.path)
                    targetDoc.update("A2", 1973)
                }
            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }


    }

    fun onStopListen(v: View) {
        a1.qry?.remove()
    }

    fun onAddData(v: View) {

        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        val datasTrips = listOf<TravelData>(
//            TravelData("Accra", "Taipei", -1, 1,"",
//                sdf.parse("2020/01/17"), sdf.parse("2020/01/19"),
//                "no comment", "promethus@gmail.com"),
//            TravelData("Taipei", "Bangkok", -1, 1,"",
//                sdf.parse("2020/01/20"), sdf.parse("2020/01/23"),
//                "no comment", "promethus@gmail.com"),
//            TravelData("Bangkok", "Boston", -1, 1,"",
//                sdf.parse("2020/01/24"), sdf.parse("2020/01/26"),
//                "no comment", "promethus@gmail.com"),
//            TravelData("Boston", "Accra", -1, 1,"",
//                sdf.parse("2020/01/27"), sdf.parse("2020/01/31"),
//                "no comment", "promethus@gmail.com"),
//
//            TravelData("Taipei", "Bangkok", -1, 1,"",
//                sdf.parse("2020/01/20"), sdf.parse("2020/01/24"),
//                "no comment", "zdv84101@bcaoo.com"),
//            TravelData("Bangkok", "Chicago", -1, 1,"",
//                sdf.parse("2020/01/25"), sdf.parse("2020/01/28"),
//                "no comment", "zdv84101@bcaoo.com"),
//            TravelData("Chicago", "Accra", -1, 1,"",
//                sdf.parse("2020/01/29"), sdf.parse("2020/01/31"),
//                "no comment", "zdv84101@bcaoo.com"),

            TravelData(
                "Berlin", "Boston", -1, 1, "",
                sdf.parse("2020/02/22"), sdf.parse("2020/02/23"),
                "no comment", "d835115@urhen.com"
            ),
            TravelData(
                "Boston", "Bangkok", -1, 1, "",
                sdf.parse("2020/02/24"), sdf.parse("2020/02/27"),
                "no comment", "d835115@urhen.com"
            ),
            TravelData(
                "Bangkok", "Chicago", -1, 1, "",
                sdf.parse("2020/02/28"), sdf.parse("2020/03/01"),
                "no comment", "d835115@urhen.com"
            )
        )

        val userList = listOf<UserInfo>(
            UserInfo(
                "d835115@urhen.com", "d835115", sdf.parse("1971/03/05"),
                Gender.M, "", "", "", sdf.parse("2020/01/10")
            ),
            UserInfo(
                "zdv84101@bcaoo.com", "zdv84101", sdf.parse("1971/05/05"),
                Gender.M, "", "", "", sdf.parse("2020/01/14")
            )
        )

        val db = FirebaseFirestore.getInstance()
        val u1id = "d835115@urhen.com"
        val u2id = "zdv84101@bcaoo.com"
        val user1 = db.collection("users").document(u1id)
        val user2 = db.collection("users").document(u2id)
        val coll = db.collection("trips")
        val docL = ArrayList<DocumentReference>()

        db.runBatch { bc ->
            //            bc.set(user1, userList[0])
//            bc.set(user2, userList[1])

            for (i in datasTrips) {
                val d = db.collection("trips").document()
                bc.set(d, i)
            }

        }.addOnSuccessListener {
            Log.d(TAG, "success")
        }.addOnFailureListener {
            Log.d(TAG, it.message.toString())
        }
    }
//    fun onAddData(v: View) {
//        val sdf = SimpleDateFormat("yyyy/MM/dd")
//        val now = Date()
//        val init = sdf.format(now)
//        Log.d(TAG, "now=$init")
//        val cal = Calendar.getInstance()
//        cal.time = now
//        cal.add(Calendar.DAY_OF_MONTH, 1)
//        val now2 = cal.time
//        Log.d(TAG, "now=${sdf.format(cal.time)}")
//
//        cal.add(Calendar.DAY_OF_MONTH, -2)
//        val now3 = cal.time
//        Log.d(TAG, "now=${sdf.format(cal.time)}")
//        val cmp = now2.compareTo(now3)
//        Log.d(TAG, "result of comparing=$cmp")
//    }

    class Person(val name: String, val name2: String, var age: Int) {
        fun present() = "I'm $name, and I'm $age years old"
        fun greet(other: String) = "Hi, $other, I'm $name"
    }

    fun <T> printProperty(instance: T, prop: KProperty1<T, *>) {
        println("${prop.name} = ${prop.get(instance)}")
    }

    fun <T> incrementProperty(
        instance: T, prop: KMutableProperty1<T, Int>
    ) {
        val value = prop.get(instance)
        prop.set(instance, value + 1)
    }

    fun onTest(v: View) {

        val sum: (Int, Int) -> Int = { a, b -> a + b }
    }

    fun onAddSimuTrips(v: View) {
        val cal = Calendar.getInstance()
        val cities = listOf<String>("Berlin", "Taipei", "Bangkok", "Chicago", "Accra", "Boston")

        val nCitiesIdx = cities.count() - 2
        val users = ArrayList<UserInfo>()
        val db = FirebaseFirestore.getInstance()
        db.collection("users").whereLessThan("register", cal.time).get()
            .addOnSuccessListener { docs ->
                db.runBatch { bc ->
                    try {
                        for (i in docs.documents) {

                            val user = i.toObject<UserInfo>()
                            if (user == null) continue
                            users.add(user)
                            cal.set(2020, 2, 3)
                            val cis = cities.shuffled()
                            for (j in 0..nCitiesIdx) {

                                val d1 = cal.time
                                cal.add(Calendar.DAY_OF_MONTH, 2)
                                val d2 = cal.time
                                val tdata = TravelData(
                                    cis[j], cis[j + 1], -1, 1, dDepart = d1, dArrive = d2,
                                    emailID = user.id
                                )
                                val d = db.collection("trips").document()
                                bc.set(d, tdata)
                            }
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, e.message.toString())
                    }
                }.addOnSuccessListener {
                    Log.d(TAG, "run batch success")
                }


            }.addOnFailureListener {

        }
    }


    fun dataScope(d: Date, tolerence: Int): Array<Date> {

        val cal = Calendar.getInstance()
        cal.time = d


        cal.add(Calendar.DAY_OF_MONTH, -tolerence)

        val t1 = cal.time
        cal.add(Calendar.DAY_OF_MONTH, 2 * tolerence)
        val t2 = cal.time
        return arrayOf<Date>(t1, t2)

    }

    fun onMakeIndex4Query(v: View) {
        val db = FirebaseFirestore.getInstance()
        val cal = Calendar.getInstance()
        val now = cal.time
        val coll = db.collection("trips")
        val q = coll.limit(100)
            .get().addOnSuccessListener {
                val td = it.documents[0].toObject<TravelData>()
                val ds1 = this@Main2Activity.dataScope(td!!.dDepart, 2)
                val ds2 = this@Main2Activity.dataScope(td!!.dArrive, 2)
                val fields = arrayOf<String>("ddepart", "darrive")

                var i = 0


//                try {
                coll.limit(50)
                    .whereGreaterThanOrEqualTo(fields[0], ds1[0])
//                        .whereLessThanOrEqualTo(fields[0],ds1[1])
//                        .whereGreaterThanOrEqualTo(fields[1], ds2[0])
                    .whereLessThanOrEqualTo(fields[i], ds2[i])
                    .whereEqualTo("cityTo", td.cityTo)
                    .whereEqualTo("cityFrom", td.cityFrom)
                    .get().addOnSuccessListener {

                        val sdf = SimpleDateFormat("yyyy/MM/dd")
                        for (i in it.documents) {
                            val td = i.toObject<TravelData>()
                            Log.d(
                                TAG,
                                "uid=${td!!.emailID}, d=${sdf.format(td.dDepart)}, a=${sdf.format(td.dArrive)}"
                            )
                            Log.d(TAG, "cityTo=${td.cityTo}")
                        }
                    }.addOnFailureListener {
                        Log.d(TAG, it.message.toString())
                    }
//                } catch (e: Exception) {
//                    Log.d(TAG,e.message.toString())
//                }

            }
    }

    fun onOpenCalendar(v: View) {
        if (calendarView.visibility == View.GONE)
            calendarView.visibility = View.VISIBLE
        else
            calendarView.visibility = View.GONE
    }
}


