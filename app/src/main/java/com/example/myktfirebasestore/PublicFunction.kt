package com.example.myktfirebasestore

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.view.View
import android.widget.DatePicker
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

interface DatePickerCallback {
    fun onGetDateString(str: String)
}

class DateConvertor(val ctx: Context) {

    private var sdf: SimpleDateFormat

    init {
        sdf = SimpleDateFormat(ctx.resources.getString(R.string.str_date_format), Locale.TAIWAN)
    }

    fun dateToString(data: Date): String {
        return sdf.format(data)
    }

    fun stringToDate(strDate: String): Date {

        val date = sdf.parse(strDate)
        if (date == null) throw Exception("stringToDate error")
        return date
    }

}
class HelpObject(val ctx: Context, val titleMsgId: Int, val contentId: Int) : View.OnClickListener {
    override fun onClick(p0: View?) {

        showMsgDlg(ctx, titleMsgId, contentId)
    }

    companion object {
        //        fun setPrefUserId(ct: Context, uid:String){
//            //get preference form file
//            val pref =
//                ct.getSharedPreferences(ct.resources.getString(R.string.str_pref_name), Context.MODE_PRIVATE)
//            val tagEmailID = ct.resources.getString(R.string.str_uid_tag)
//            pref.edit().putString(tagEmailID, uid).commit()
//        }
//        fun getPrefUserId(ct: Context): String? {
//
//            //get preference form file
//            val pref =
//                ct.getSharedPreferences(ct.resources.getString(R.string.str_pref_name), Context.MODE_PRIVATE)
//
//            // id from preference
//            val tagEmailID = ct.resources.getString(R.string.str_uid_tag)
//            return pref?.getString(tagEmailID, "")
//        }
        fun uploadImage(
            bitmap: Bitmap,
            context: Context,
            uid: String,
            filename: String,
            f: () -> Unit
        ) {


            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data: ByteArray = baos.toByteArray()

            val res = context.resources
            val gs: String = res.getString(R.string.google_storage_bucket)
            val childPath: String =
                res.getString(R.string.google_storage_bucket_face_photos_dir) + uid + "/"
            val targetName = childPath + filename

            val storage = FirebaseStorage.getInstance(gs)
            val imgRef = storage.reference.child(targetName)


            imgRef.putBytes(data).addOnFailureListener {
                // Handle unsuccessful uploads
                it.printStackTrace()
                throw it
            }.addOnSuccessListener { _ ->
                f()
            }
        }

        fun documents2AListForQueryTrips(qrySS: QuerySnapshot): ArrayList<WrapTravelData> {
            val alist = ArrayList<WrapTravelData>()
            if (qrySS.isEmpty == false) {

                for (doc in qrySS.documents) {
                    val d = doc?.toObject<TravelData>()
                    if (d != null) alist.add(WrapTravelData(d, doc.reference.path))
                }

            }
            return alist
        }

        fun showMsgDlg(ct: Context, strTitle: Int, strCnt: Int) {
            AlertDialog.Builder(ct)
                .setTitle(ct.resources.getString(strTitle))
                .setMessage(ct.resources.getString(strCnt))
                .setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }
                }).show()
        }




        fun pickDate(ctx: Context, callback: DatePickerCallback) {
            val cal = Calendar.getInstance()
            val y = 2002
            val m = cal.get(Calendar.MONTH)
            val d = cal.get(Calendar.DAY_OF_MONTH)


            DatePickerDialog(ctx, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {

                    if (p0 != null && p0.isShown)
                        callback.onGetDateString("$p1/${p2 + 1}/$p3")
                }
            }, y, m, d)
                .show()
        }

        fun getCompoundRoomName(u1: String, u2: String): String {
            return if (u1.compareTo(u2) < 0) "${u1}|${u2}" else "${u2}|${u1}"
        }
    }
}