package com.example.myktfirebasestore

import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*


interface FFGet {
    fun returnDoc(doc: DocumentSnapshot)
}

class FFOperation {


    companion object {
        private val db = FirebaseFirestore.getInstance()

        fun getDoc(path: String) {
//            db.collection()
        }
    }

}

class A1(val hdle: Handler) : Runnable {
    override fun run() {

        val data = hashMapOf<String, Any>(
            "id" to "aaa@a.com",
            "A1" to "are you ok",
            "A2" to 1972,
            "TimeZone" to TimeZone.getDefault().displayName

        )
        var res = ""
        val path = "cities/test"
        val db = FirebaseFirestore.getInstance()
//        db.document(path).set(data, SetOptions.merge()).addOnSuccessListener {
//            res="success"
//        }.addOnFailureListener {
//            res=it.message.toString()
//        }

        db.collection("cities").add(data).addOnSuccessListener {
            res = "success"
        }.addOnFailureListener {
            res = it.message.toString()
        }
        val msg = hdle.obtainMessage()
        msg.what = R.id.A1
        val bu = Bundle().apply {
            putString(R.id.A1.toString(), res)
        }
        msg.data = bu
        hdle.sendMessage(msg)


    }

}