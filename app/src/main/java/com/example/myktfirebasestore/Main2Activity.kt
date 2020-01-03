package com.example.myktfirebasestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import java.lang.Exception
import java.util.*

data class User2(
    var id: String = "", var name: String = "", var birthday: Date = Date(),
    var gender: Int = 0, var language: String = "",
    var country: String = "", var desp: String = ""
)

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
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
}
