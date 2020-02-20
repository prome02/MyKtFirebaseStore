package com.example.myktfirebasestore

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import java.io.ByteArrayOutputStream

@Parcelize
class WrapTravelData(var docPath: String) : TravelData() {
    constructor(tra: TravelData, docPath: String) : this(docPath) {
        cityFrom = tra.cityFrom
        cityTo = tra.cityTo
        id = tra.id
        transpotation = tra.transpotation
        transInfo = tra.transInfo
        dDepart = tra.dDepart
        dArrive = tra.dArrive
        description = tra.description


        emailID = tra.emailID
    }
}

@Parcelize
data class WrapTravelDatas(val list: ArrayList<WrapTravelData>, var userinfo: UserInfo?) :
    Parcelable

class M3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_m3)
    }

    fun uploadImage(bitmap: Bitmap, gs: String, childPath: String, f: () -> Unit) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl(gs)
        val imagesRef = storageRef.child(childPath)
        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            it.printStackTrace()
            throw it
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//            val downloadUrl: Uri = taskSnapshot.uploadSessionUri
            // Do what you want
            f()
        }
    }

    fun onUploadBitmp(v: View) {
//        try {
//            val bm:Bitmap
//            findViewById<ImageView>(R.id.iv_face).apply {
//                bm=(drawable as BitmapDrawable).bitmap
//                uploadImage(bm, "gs://myktapp-f93c6.appspot.com/","images/test.jpg",
//                    {})
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }


        val storage = FirebaseStorage.getInstance("gs://myktapp-f93c6.appspot.com")
        val imgPath = "images/promethus@gmail.com/main.jpg"
        val imgRef = storage.reference.child(imgPath)
        val iv = findViewById<ImageView>(R.id.iv_face)

        var bm: Bitmap? = null
        if (iv.drawable is BitmapDrawable) {
            bm = (iv.drawable as BitmapDrawable).bitmap
        } else return


        val baos = ByteArrayOutputStream()
        bm?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        var uploadTask = imgRef.putBytes(data)

        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            it.printStackTrace()
        }


    }

    lateinit var faceObj: FacePhotoCtrl
    fun onSelectImg(v: View) {
//        val opts = FirebaseApp.getInstance().options
//        Log.i("msg", "Bucket = " + opts.storageBucket)
        try {
            faceObj = FacePhotoCtrl(this, findViewById<ImageView>(R.id.iv_face))
            faceObj.send2Act(arrayOf("from camera", "from gallary"), R.string.face_title)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        faceObj.onActivityResult(requestCode, resultCode, data)
    }

    fun onSendUserTrip(v: View) {
        val uid = "promethus@gmail.com"
        val intent = Intent(this@M3, BrowserUserAct::class.java)
        //my id
        val myid = "d1881740@urhen.com"

        val obj = UserInfo(id = uid, name = "unknown")
        intent.putExtra(BrowserUserAct::class.java.simpleName, myid)

        intent.putExtra(BrowserUserAct.sIntentTag, Bundle().apply {
            putParcelable(BrowserUserAct.sIntentTag, obj)
        })
//        intent.putStringArrayListExtra(BrowserUserAct::class.java.simpleName, ary)
//        intent.putExtra(BrowserUserAct::class.java.simpleName, uid)
        startActivity(intent)

//        val list = ArrayList<WrapTravelData>()
//        var userinfo: UserInfo?
//
//        val db = FirebaseFirestore.getInstance()
//        db.collection("trips").whereEqualTo("eailID", uid).get()
//            .addOnSuccessListener {
//
//
//                for (i in it.documents) {
//                    val obj = i.toObject<TravelData>()
//                    val wobj = WrapTravelData(obj!!, i.reference.path)
//                }
//
//                db.collection("users").document(uid).get()
//                    .addOnSuccessListener {
//                        userinfo = it.toObject<UserInfo>()
//
//                        val wraps = WrapTravelDatas(list, userinfo)
//                        val bu = Bundle()
//                        bu.putParcelable(WrapTravelDatas::class.java.simpleName, wraps)
//                        val intent = Intent(this@M3, BrowserUserAct::class.java)
//                        intent.putExtra(WrapTravelDatas::class.java.simpleName, bu)
//                        startActivity(intent)
//                    }
//            }
    }

    fun onTvReturn(v: View) {
        findViewById<TextView>(R.id.textView).apply {
            setTextSize(18.toFloat())
            val s = "this is test<br> <big>is</big> it <small>right</small>?<p> yes"

            setText(HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE))
        }
    }

    fun onDownloadFacePhoto(v: View) {
        val storage = FirebaseStorage.getInstance()
        val imgPath = "/images/promethus@gmail.com/main.jpg"
        val ImgRef = storage.reference.child(imgPath)
        val iv = findViewById<ImageView>(R.id.iv_face)
        val ONEMEGA = (1024 * 1024).toLong()
        ImgRef.getBytes(ONEMEGA).addOnFailureListener {

        }.addOnSuccessListener { btsAry ->
            val bmp = BitmapFactory.decodeByteArray(btsAry, 0, btsAry.size)
            iv.setImageBitmap(bmp)

        }
    }
}
