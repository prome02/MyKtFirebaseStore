package com.example.myktfirebasestore

import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.firestore.*
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap


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
    var qry: ListenerRegistration? = null
    override fun run() {

        val strID = "id"
        val email = "promethus@gmail.com"
        val data = hashMapOf<String, Any>(
            strID to email,
            "A1" to "are you ok",
            "A2" to 1972,
            "TimeZone" to TimeZone.getDefault().displayName

        )
        var res = ""
        val path = "trips"
        val db = FirebaseFirestore.getInstance()
//        db.document(path).set(data, SetOptions.merge()).addOnSuccessListener {
//            res="success"
//        }.addOnFailureListener {
//            res=it.message.toString()
//        }
////////////////////////////////////////////////////////////

//        try {
//            db.collection(path).add(data).addOnSuccessListener {
//                res = "success"
//            }.addOnFailureListener {
//                res = it.message.toString()
//            }
//        } catch (e: Exception) {
//
//        }
////////////////////////////////////////////////////////////


        qry = db.collection(path).whereEqualTo(strID, email)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }
                val source = if (querySnapshot != null && querySnapshot.metadata.hasPendingWrites())
                    "Local"
                else
                    "Server"

                if (querySnapshot?.isEmpty == false) {
                    for (dc in querySnapshot!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> Log.d(
                                "TAG",
                                "New : $source: ${dc.document.data}"
                            )
                            DocumentChange.Type.MODIFIED -> Log.d(
                                "TAG",
                                "Modified : $source: ${dc.document.data}"
                            )
                            DocumentChange.Type.REMOVED -> Log.d(
                                "TAG",
                                "Removed : $source: ${dc.document.data}"
                            )
                        }
                    }
//                    for(doc in querySnapshot!!){
//                        Log.d("TAG", "*************from $source, ${doc.toString()}")
//                    }

                    val msg = hdle.obtainMessage()
                    msg.what = R.id.A1
                    val bu = Bundle().apply {
                        //                        putParcelable(R.id.A1.toString(), )
                        putString(R.id.A1.toString(), res)
                    }
                    msg.data = bu
                    hdle.sendMessage(msg)
                }
            }

////////////////////////////////////////////////////////////
//        val msg = hdle.obtainMessage()
//        msg.what = R.id.A1
//        val bu = Bundle().apply {
//            putString(R.id.A1.toString(), res)
//        }
//        msg.data = bu
//        hdle.sendMessage(msg)


    }

}