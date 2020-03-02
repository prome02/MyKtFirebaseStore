package com.example.myktfirebasestore

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.browser_user_info.*
import java.lang.ref.WeakReference

class BrowserUserAct : AppCompatActivity() {
    val mHlr = MyHandler(this)

    companion object {
        val sIntentTag = "IntentTag"
        class MyHandler(ref: BrowserUserAct) : Handler() {
            var mOuter = WeakReference<BrowserUserAct>(ref)
            override fun handleMessage(msg: Message) {

                val act = mOuter.get()
                act ?: return
                act.mFriend ?: return
                act.mProgressBar.hideProgressBar()
                when (msg.what) {
                    R.id.SearchUsers -> {

                        val userlist = msg.data.getParcelableArrayList<UserInfo>(SearchUsers.Tag)
                        if (userlist == null || userlist.count() != 1) {
                            Toast.makeText(
                                act,
                                "can't find your personal data, or unknown error are happend",
                                Toast.LENGTH_LONG
                            )
                        } else {
                            val user = userlist[0]


//                            (act.frag_gen_sel as GenSelectFrag).apply {
//                                setState(user.gender)
//                                setEnable(false)
//                            }

                            val txt2 = act.findViewById<TextView>(R.id.txt_desp)
                            val txt1 = act.findViewById<TextView>(R.id.txt_user_content)
                            user.outputContent(act, txt1, txt2)

                            act.findViewById<TextView>(R.id.txt_desp).apply {
                                setText(user.desp)
                            }

                            act.mProgressBar.showProgressBar()
                            Thread(SearchTripsByUser(act.mHlr, user.id)).start()

                        }
                    }
                    R.id.SearchTripsByUser -> {

                        val obj =
                            msg.data.getStringArrayList(SearchTripsByUser::class.java.simpleName)
                        obj ?: return
                        val lv = act.findViewById<ListView>(R.id.lvTrips)

                        lv.adapter =
                            ArrayAdapter<String>(act, android.R.layout.simple_list_item_1, obj)

                        act.mProgressBar.showProgressBar()

                        Thread(DownloadFacePhoto(act.mHlr, act.mFriend!!.id, "main.jpg")).start()
                    }
                    R.id.DownloadFacePhoto -> {
                        if (msg.data != null) {

                            msg.data.getByteArray(DownloadFacePhoto::class.java.simpleName)?.run {
                                val bmp = BitmapFactory.decodeByteArray(this, 0, size)
                                act.findViewById<ImageView>(R.id.iv_photo).apply {
                                    setImageBitmap(bmp)
                                }
                            }
                        }

                    }
                    R.id.SearchChattingRoomByUser -> {
                        when (msg.arg1) {
                            0 -> act.createNewChattingRoom()
                            1 -> {
                                //room is existed, open it
                                act.openChattingRoom()
                            }
                            2 -> {
                                if (msg.arg2 >= 15) {
                                    Toast.makeText(
                                        act,
                                        R.string.too_many_chatting_rooms,
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else act.createNewChattingRoom()
                            }
                            else -> {
                                Log.d(
                                    "BrowserUserAct",
                                    "unknown error msg id=${msg.what.toString()}"
                                )
                            }
                        }
                    }
                    R.id.CreateNewChattingRoom -> {
                        when (msg.arg1) {
                            0 -> {
                                Toast.makeText(act, R.string.str_unknown_er, Toast.LENGTH_LONG)
                                    .show()
                            }
                            1 -> {// create new room ok.
                                act.openChattingRoom()
                            }
                            else -> {
                                Toast.makeText(act, R.string.str_unknown_er, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                }
            }
        }

        class CreateNewChattingRoom(val h: Handler, val sMyId: String, val uiFriend: UserInfo) :
            Runnable {
            override fun run() {
                val db = FirebaseFirestore.getInstance()
                //create a new room

//                val ucFriend = UserContact(name = uiFriend.name, id = uiFriend.id, roomValid = true)
//                val ucRef = db.collection("users").document(sMyId)
//                    .collection("contacts").document(uiFriend.id)

                val roomId: String = HelpObject.getCompoundRoomName(sMyId, uiFriend.id)
                val chattingRoomRef = db.collection("rooms")
                    .document(roomId)
                val roomInfo = RoomInfo(
                    name = roomId,
                    isValid = true,
                    users = arrayListOf<String>(sMyId, uiFriend.id)
                )

                db.runBatch { bt ->
                    //                    bt.set(ucRef, ucFriend)
                    bt.set(chattingRoomRef, roomInfo)
                }.addOnSuccessListener {
                    h.obtainMessage().apply {
                        what = R.id.CreateNewChattingRoom
                        arg1 = 1
                        h.sendMessage(this)
                    }


                }.addOnFailureListener {
                    val msg = h.obtainMessage().apply {
                        what = R.id.CreateNewChattingRoom
                        arg1 = 0
                    }
                    h.sendMessage(msg)
                    it.printStackTrace()
                }
            }
        }
        class DownloadFacePhoto(val h: Handler, val uid: String, val filename: String) : Runnable {
            override fun run() {
                val storage = FirebaseStorage.getInstance()
                val imgPath = "/images/$uid/$filename"
                val ImgRef = storage.reference.child(imgPath)

                val ONEMEGA = (1024 * 1024).toLong()
                ImgRef.getBytes(ONEMEGA).addOnFailureListener {
                    val msg = h.obtainMessage()
                    msg.data = null
                    msg.what = R.id.DownloadFacePhoto
                    h.sendMessage(msg)
                }.addOnSuccessListener { btsAry ->
                    val bmp = BitmapFactory.decodeByteArray(btsAry, 0, btsAry.size)
                    val bu = Bundle()
                    bu.putByteArray(DownloadFacePhoto::class.java.simpleName, btsAry)
                    val msg = h.obtainMessage()
                    msg.data = bu
                    msg.what = R.id.DownloadFacePhoto
                    h.sendMessage(msg)
                }

            }
        }

        class SearchTripsByUser(val hlr: Handler, val uid: String) : Runnable {
            override fun run() {
                FirebaseFirestore.getInstance().collection("trips").whereEqualTo("emailID", uid)
                    .get()
                    .addOnSuccessListener {

                        val ary = ArrayList<String>()
                        for (i in it.documents) {
                            i.toObject<TravelData>()?.apply {
                                ary.add(makeContentTitle())
                            }
                        }

                        val msg = hlr.obtainMessage()
                        msg.what = R.id.SearchTripsByUser
                        msg.data = Bundle().apply {
                            putStringArrayList(SearchTripsByUser::class.java.simpleName, ary)
                        }
                        hlr.sendMessage(msg)
                    }.addOnFailureListener {
                        it.printStackTrace()
                        val msg = hlr.obtainMessage()
                        msg.what = 0
                        hlr.sendMessage(msg)
                    }
            }
        }

        class SearchUsers(val h: Handler, val sUid: String) : Runnable {

            override fun run() {
                val db = FirebaseFirestore.getInstance()
                val userlist = ArrayList<UserInfo>()
                db.collection("users").whereEqualTo("id", sUid).get().addOnSuccessListener {
                    for (i in it.documents) {
                        val ob = i?.toObject(UserInfo::class.java)
                        if (ob != null) userlist.add(ob)
                    }
//                    if (userlist.count() == 0) return@addOnSuccessListener
                    val msg = h.obtainMessage()
                    msg.what = R.id.SearchUsers

                    val bu = Bundle().apply {
                        putParcelableArrayList(Tag, userlist)
                    }
                    msg.data = bu
                    h.sendMessage(msg)
                }.addOnFailureListener {
                    Log.d("SearchUsers", it.message.toString())
                    val msg = h.obtainMessage()
                    msg.what = 0
                    h.sendMessage(msg)
                }
            }

            companion object {
                //                val TagNum = R.id.SearchUsers
                val Tag = SearchUsers::class.java.simpleName
            }
        }

        class SearchChattingRoomByUser(val h: Handler, val uid: String, val friendId: String) :
            Runnable {
            override fun run() {
                val db = FirebaseFirestore.getInstance()

//                val sRoomName=HelpObject.getCompoundRoomName(uid,friendId)
                db.collection("rooms").whereArrayContains("users", uid).get()
                    .addOnSuccessListener {
                        val idName = HelpObject.getCompoundRoomName(uid, friendId)
                        var isFound = false
                        for (i in it.documents) {
                            val roomInfo = i.toObject<RoomInfo>()
                            if (roomInfo?.name?.compareTo(idName) == 0) {
                                isFound = true
                                break
                            }
                        }
                        h.obtainMessage().apply {
                            what = R.id.SearchChattingRoomByUser
                            if (isFound) {
                                arg1 = 1
                            } else {
                                arg1 = 2
                                arg2 = it.documents.count()
                            }
                            h.sendMessage(this)
                        }
                    }.addOnFailureListener {
                        h.obtainMessage().apply {
                            what = R.id.SearchChattingRoomByUser
                            arg1 = 0
                            h.sendMessage(this)
                        }
                    }

//                db.collection("users").document(uid)
//                    .collection("contacts").get()
//                    .addOnFailureListener {
//                        val msg = h.obtainMessage()
//                        msg.what = R.id.SearchChattingRoomByUser
//                        msg.arg1 = 0
//                        h.sendMessage(msg)
//                    }.addOnSuccessListener {
//                        val msg = h.obtainMessage()
//                        msg.arg1 = 0
//                        msg.arg2 = it.documents.count()
//                        msg.what = R.id.SearchChattingRoomByUser
//                        var isUser: Boolean = false
//                        for (i in it.documents) {
//                            val obj = i.toObject<UserInfo>()
//                            if (obj?.id?.compareTo(friendId) == 0) {
//                                isUser = true
//                                break
//                            }
//                        }
//                        if (isUser) {
//                            msg.arg1 = 1
//                            msg.data = Bundle().apply {
//                                putString(SearchChattingRoomByUser::class.java.simpleName, friendId)
//                            }
//                        }
//
//                        h.sendMessage(msg)
//                    }
            }
        }
    }

    lateinit var mProgressBar: ProgressCtrl

    lateinit var mImgRef: StorageReference
    var mFriend: UserInfo? = null
    lateinit var msMyId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.browser_user_info)

        mProgressBar = ProgressCtrl(findViewById<ProgressBar>(R.id.progressBar))

        intent.getStringExtra(BrowserUserAct::class.java.simpleName)?.apply {
            msMyId = this
        }

        intent.getBundleExtra(BrowserUserAct.sIntentTag)?.let {

            mFriend = it.getParcelable<UserInfo>(BrowserUserAct.sIntentTag)
            mFriend?.apply {
                mProgressBar.showProgressBar()
                Thread(SearchUsers(mHlr, id)).start()
            }
        }











        create_room.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mFriend?.run {
                    Thread(SearchChattingRoomByUser(mHlr, msMyId, id)).start()
                }

            }
        })

    }

    val refStamp = "ImgRef"
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        mImgRef.let {
            outState.putString(refStamp, it.toString())
        }
    }

    fun openChattingRoom() {
        mFriend?.let {
            val intent = Intent(this, ChattingActivity::class.java)
            val names = arrayListOf<String>(msMyId, it.id)
            intent.putStringArrayListExtra(ChattingActivity::class.java.simpleName, names)
            startActivity(intent)
        }

    }

    fun createNewChattingRoom() {
        //create a new room
        val friendInfo = mFriend
        mProgressBar.showProgressBar()
        Thread(
            CreateNewChattingRoom(
                mHlr,
                msMyId,
                friendInfo!!
            )
        ).start()
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val ref = savedInstanceState.getString(refStamp) ?: return
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(ref)

        val tasks = storageRef.activeDownloadTasks

        tasks.size.let { it ->
            if (it > 0) {
                val task = tasks[0]
                task.addOnSuccessListener {


                }.addOnFailureListener {

                }
            }
        }
    }
}
