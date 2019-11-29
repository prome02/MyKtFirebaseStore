package com.example.myktfirebasestore

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
//import com.firebase.ui.auth.AuthUI
//import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlin.io.println as println1

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
    val remoteConfig = FirebaseRemoteConfig.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(10)
            .build()

        remoteConfig.setConfigSettingsAsync(configSettings).addOnSuccessListener {
            Log.d(TAG, "setConfigSettingsAsync ok")
        }.addOnFailureListener {
            Log.d(TAG, "setConfigSettingsAsync not ok")
        }

        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

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
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "DocumentSnapshot added with ID: ${documentReference.id}")
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

        val test : String = remoteConfig.getString(resources.getString(R.string.str_rconfig_start_page))
        Log.d(TAG, "onClickGetString = $test")

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
                    val tv: TextView =findViewById(R.id.txtResult)
                    val res: String="DocumentSnapshot data: ${document.data}"
                    tv.text= res
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
//            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser

                val tv : TextView=this.findViewById(R.id.txtResult)
                val email : String?= user?.email ?: null
                if(email != null) tv.setText( "email : $email")
                else tv.setText("email denied")

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
