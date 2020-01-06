package com.example.myktfirebasestore

//import com.firebase.ui.auth.AuthUI
//import com.firebase.ui.auth.IdpResponse
//import com.google.firebase.auth.FirebaseAuth

import android.R.attr.apiKey
import android.R.attr.dialogLayout
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*


data class City(
    val name: String? = null,
    val state: String? = null,
    val country: String? = null,
    val isCapital: Boolean? = null,
    val population: Long? = null,
    val regions: List<String>? = null
)
class MainActivity : AppCompatActivity() {
    val TAG : String ="err"
    val RC_SIGN_IN : Int =2
    val AUTOCOMPLETE_REQUEST_CODE: Int = 152
    val remoteConfig = FirebaseRemoteConfig.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(10)
            .build()

        remoteConfig.setConfigSettingsAsync(configSettings).addOnSuccessListener {
            Log.d(TAG, "setConfigSettingsAsync ok")
        }.addOnFailureListener {
            Log.d(TAG, "setConfigSettingsAsync not ok")
        }

//        HelpObject.showDlg(this, "test", "test")
        FirebaseApp.initializeApp(this)

        // Initialize the SDK
        Places.initialize(applicationContext, "AIzaSyCFA_hiLEUXb8kXfzisDM7XOt9CmkNMCDE")

        // Create a new Places client instance
        val placesClient: PlacesClient = Places.createClient(this)


    }

    fun onGetGender(v: View) {
        val gf = frag_gen_sel as GenSelectFrag
        val sta = gf.getState()
        Log.d(TAG, "gen=$sta")
    }
    override fun onStart() {
        super.onStart()


    }
    override fun onResume() {
        super.onResume()

//        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
//
//        val configSettings = FirebaseRemoteConfigSettings.Builder()
//            .setMinimumFetchIntervalInSeconds(3600)
//            .build()
//        remoteConfig.setConfigSettingsAsync(configSettings)
//        remoteConfig.getString( )
    }
    fun onClickRead( v: View){
        val tv : TextView =findViewById(R.id.txtResult)

        val db = FirebaseFirestore.getInstance()
//        var res : String=" "
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val data  = document.data
                    var name : String = data["name"].toString()
                    kotlin.io.println( "this is $name ")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
    fun onClickListener(v: View) {

        val txt = findViewById<EditText>(R.id.edtName)
        // Create a new user with a first and last name
        var name : String = txt.text.toString()


        val user : HashMap<String, Any> = HashMap<String, Any>()

        user.put("name", name)
//        user.put("name", cname)
        user.put("born", 1972)


        val tag: String = resources.getString(R.string.str_log_tag)
// Add a new document with a generated ID

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(name)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "DocumentSnapshot added with ID: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error adding document", e)
            }
    }



    fun ocClickAuth( v : View){
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
//            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
//            AuthUI.IdpConfig.FacebookBuilder().build()
//            AuthUI.IdpConfig.TwitterBuilder().build()
        )

//        AuthUI.IdpConfig.EmailBuilder().build()
// Create and launch sign-in intent
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }

    fun onClickGetString(v: View) {
//        val key=resources.getString(R.string.str_rconfig_start_page)
//        remoteConfig.getString(key)
//        val gson=GsonBuilder()
//        val cities=gson.create().fromJson<List<String>>(value, String::class.java)
//


    }






    fun onClickInitListViewData(v: View){
        ryc.apply {
            setHasFixedSize(true)
            adapter=MyAda()
            layoutManager=LinearLayoutManager(this.context)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.HORIZONTAL))

        }
        ryc.adapter?.notifyDataSetChanged()




    }
    fun onClickWriteCitiesData(v: View){
//        val db=getFFI()
//        val cities = db.collection("cities")
//        val citiesAry=resources.getStringArray(R.array.cities_en)
//
//
////        val citiesLst=citiesAry.toList()
//        val data= hashMapOf("cities_en" to citiesAry.toList())
//
//
//        cities.document("cities_en").set(data)
//            .addOnSuccessListener {doc ->
//            val str="cities_zh write into ok"
//            Log.d(TAG, str)
//            txtResult.text=str
//        }.addOnFailureListener {ex->
//            txtResult.text= ex.message
//            Log.d(TAG, ex.message)
//        }
    }

    fun onClockReadFFArray(v: View){
        val db=getFFI()
        val str="cities_en"
        db.collection("cities").document(str)
            .get().addOnSuccessListener { doc->
                val ma=doc.data as HashMap<String, List<String?>>
                val lst= ma[str]
                if(lst!=null){

                    for(item in lst){

                        if(item!=null) println(item)
                    }
                }

            }
            .addOnFailureListener {
                Log.d("TAG", "msg ${it.message}; local msg:${it.localizedMessage}")
            }
    }
    fun onClickDlg(v: View){
class ADA: ArrayAdapter<String>{
    constructor(context: Context, resource: Int) : super(context, resource)
    constructor(context: Context, resource: Int, textViewResourceId: Int) : super(
        context,
        resource,
        textViewResourceId
    )

    constructor(context: Context, resource: Int, objects: Array<out String>) : super(
        context,
        resource,
        objects
    )

    constructor(
        context: Context,
        resource: Int,
        textViewResourceId: Int,
        objects: Array<out String>
    ) : super(context, resource, textViewResourceId, objects)

    constructor(context: Context, resource: Int, objects: MutableList<String>) : super(
        context,
        resource,
        objects
    )

    constructor(
        context: Context,
        resource: Int,
        textViewResourceId: Int,
        objects: MutableList<String>
    ) : super(context, resource, textViewResourceId, objects)

//    val aryCities= resources.getStringArray(R.array.cities_zh)

    override fun getItem(position: Int): String? {

//        return aryCities[position]
        return "null"
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
//        return aryCities.count()
        return 0
    }
}
        val adap=ADA( this, R.layout.lyt_dlg_itemview, R.id.textViewxxx)

        val dlg = AlertDialog.Builder(this)


        val listener = object :DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when( p1){
                    BUTTON_NEGATIVE -> {
                        txtResult.text="you click cancel"
                        p0?.cancel()
                    }
                    else ->{

                        txtResult.text="you select item: ${adap.getItem(p1)}"
                    }
                }
            }
        }


        var adapter = dlg.setAdapter(adap, listener)
        dlg.setNegativeButton(android.R.string.cancel, listener)
        dlg.show()

    }
    fun onClickWriteData(v: View){
        val db = FirebaseFirestore.getInstance()
        val cities = db.collection("cities")

        val data1 = hashMapOf(
            "name" to "San Francisco",
            "state" to "CA",
            "country" to "USA",
            "capital" to false,
            "population" to 860000,
            "regions" to listOf("west_coast", "norcal")
        )
        cities.document("SF").set(data1)

        val data2 = hashMapOf(
            "name" to "Los Angeles",
            "state" to "CA",
            "country" to "USA",
            "capital" to false,
            "population" to 3900000,
            "regions" to listOf("west_coast", "socal")
        )
        cities.document("LA").set(data2)

        val data3 = hashMapOf(
            "name" to "Washington D.C.",
            "state" to null,
            "country" to "USA",
            "capital" to true,
            "population" to 680000,
            "regions" to listOf("east_coast")
        )
        cities.document("DC").set(data3)

        val data4 = hashMapOf(
            "name" to "Tokyo",
            "state" to null,
            "country" to "Japan",
            "capital" to true,
            "population" to 9000000,
            "regions" to listOf("kanto", "honshu")
        )
        cities.document("TOK").set(data4)

        val data5 = hashMapOf(
            "name" to "Beijing",
            "state" to null,
            "country" to "China",
            "capital" to true,
            "population" to 21500000,
            "regions" to listOf("jingjinji", "hebei")
        )
        cities.document("BJ").set(data5)

        val tv: TextView =findViewById(R.id.txtResult)
        tv.text= "write data ok"
    }

    fun onClickGetDoc(v: View){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("cities").document("SF")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val res: String="DocumentSnapshot data: ${document.data}"
                    txtResult.text= res
                    Log.d(TAG, res)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun onClickGetManyDoc(v: View){
        val db = FirebaseFirestore.getInstance()
        db.collection("cities")
            .whereEqualTo("capital", true)
            .get()
            .addOnSuccessListener { documents ->
                var res: String=" "
                for (document in documents) {
                    res+=" + ${document.id} => ${document.data}"
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
                val tv: TextView =findViewById(R.id.txtResult)
                tv.text= res
                Log.d(TAG, res)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

//    fun setToResultView(str :String){
//        val tv: TextView =findViewById(R.id.txtResult)
//        tv.text=str
//    }
    fun onClickAutocompleteCites(v: View){


// Set the fields to specify which types of place data to
// return after the user has made a selection.
    val fields = listOf(Place.Field.ID, Place.Field.NAME)

    val intent =Autocomplete.IntentBuilder( AutocompleteActivityMode.FULLSCREEN, fields).build(this)

    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
}
    fun onClickCompoundQry(v: View){
        val db = FirebaseFirestore.getInstance()
        val citiesRef = db.collection("cities")
        citiesRef.whereEqualTo("state","CO").whereEqualTo("name", "Denver")
            .get().addOnSuccessListener { docs->
                val si =docs.size()
                Log.d(TAG,"found $si doc")
                var res : String=""
                for(doc in docs){
                    val str =doc.data["state"]
                    res+= "+ $str"
                }
                txtResult.text=res
            }
            .addOnFailureListener { excep ->
                Log.w(TAG, "err", excep)
            }
    }

    fun onClickGetStrAry(v: View){
//        val strAry= resources.getStringArray(R.array.cities_zh)
//        for (str in strAry) txtResult.append(str)
    }
    fun getFFI(): FirebaseFirestore= FirebaseFirestore.getInstance()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            AUTOCOMPLETE_REQUEST_CODE -> when(resultCode){
                Activity.RESULT_OK -> {
                    if(data != null) {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        val res="place ${place.name} , ${place.id}"
                        Log.d(TAG, res)
                        txtResult.text=res
                    }else {
                        txtResult.text=" Autocomplete err"
                    }
                }
                AutocompleteActivity.RESULT_ERROR->{

                    txtResult.text=" AutocompleteActivity.RESULT_ERROR"
                }
                else ->{
                    txtResult.text=" unknown err"
                }
            }
        }
        if (requestCode == RC_SIGN_IN) {
//            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser


                val tv : TextView=this.findViewById(R.id.txtResult)
                val email : String?= user?.email ?: null
                if(email != null) txtResult.text= "email : $email"
                else txtResult.text="email denied"

                val db=getFFI()
                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener {result ->

                    }
                    .addOnFailureListener { exception ->

                    }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


}
