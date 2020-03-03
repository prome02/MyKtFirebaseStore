package com.example.myktfirebasestore

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.chatting_activity.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

data class RylVItemData(val sender: Int = -1, val msg: String = "", val time: Date = Date())
class RylV(val ctx: Context, val alDatas: ArrayList<RylVItemData>) :
    RecyclerView.Adapter<RylV.ViewHolder2>() {


    inner class ViewHolder2(view: View) : RecyclerView.ViewHolder(view) {
        val txtv: TextView
        //        val txtRight:TextView
        val llyt: LinearLayout

        init {
            view.apply {
                txtv = findViewById<TextView>(R.id.txt_left)
//                txtRight=findViewById<TextView>(R.id.txt_right)
                llyt = findViewById<LinearLayout>(R.id.llyt)
            }
        }
    }

    override fun getItemCount() = alDatas.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder2 {
        val v = LayoutInflater.from((parent.context))
            .inflate(R.layout.rylv_item, parent, false)

        return ViewHolder2(v)
    }

    fun setItem(holder: ViewHolder2, msg: RylVItemData) {

        val t1 = holder.txtv
        val llp = LinearLayout.LayoutParams(t1.layoutParams)

        when (msg.sender) {
            0 -> {
                with(t1) {
                    setBackgroundColor(ContextCompat.getColor(ctx, R.color.text_color))
                    setTextColor(ContextCompat.getColor(ctx, R.color.primary))
                }
                llp.gravity = Gravity.START
            }

            1 -> {
                with(t1) {
                    setBackgroundColor(ContextCompat.getColor(ctx, R.color.primary))
                    setTextColor(ContextCompat.getColor(ctx, R.color.text_color))
                }
                llp.gravity = Gravity.END
            }
            else -> {
                return
            }
        }

        t1.layoutParams = llp
        t1.text = msg.msg

    }

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        val msg = alDatas[position]
        holder.run {
            setItem(holder, msg)
        }


    }
}

@Parcelize
data class RoomMessage(val msg: String = "", val sender: String = "", val time: Date = Date()) :
    Parcelable

class ChattingActivity : AppCompatActivity() {

    lateinit var mProgressBar: ProgressCtrl

    companion object {
        class MyHandler(val ref: ChattingActivity) : Handler() {

            var mOuter = WeakReference<ChattingActivity>(ref)
            override fun handleMessage(msg: Message) {

                val act = mOuter.get()

                act?.mProgressBar?.hideProgressBar()

                when (msg.what) {
                    R.id.GetMessages -> {
                        when (msg.arg1) {
                            1 -> {
                                msg.data.getParcelableArrayList<RoomMessage>(GetMessages::class.java.simpleName)
                                    ?.let {
                                        act?.run {
                                            for (i in it) {

                                                val sender =
                                                    if (mMyId.compareTo(i.sender) == 0) 1 else 0
                                                val item = RylVItemData(sender, i.msg)
                                                adaDatas.add(item)
                                            }

                                            act.msglist.adapter?.notifyDataSetChanged()
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }

        class GetMessages(val hlr: MyHandler, val sMyId: String, val sFriend: String) : Runnable {
            var listening: ListenerRegistration? = null
            override fun run() {
                val sRoom = HelpObject.getCompoundRoomName(sMyId, sFriend)

                val db = FirebaseFirestore.getInstance()
                listening = db.collection("rooms").document(sRoom).collection("messages")
                    .limit(100).orderBy("time", Query.Direction.DESCENDING)
                    .addSnapshotListener { qry, e ->
                        if (e != null || qry == null || qry.isEmpty == true || qry.documentChanges.count() == 0) {
                            hlr.sendMessage(hlr.obtainMessage().apply {
                                what = R.id.GetMessages
                                arg1 = 0
                            })

                        } else {
                            try {
                                val ary = ArrayList<RoomMessage>()
                                for (i in qry.documentChanges) {
                                    if (i.type == DocumentChange.Type.ADDED) i.document.toObject<RoomMessage>().run {
                                        ary.add(this)
                                    }


                                }

                                hlr.sendMessage(hlr.obtainMessage().apply {
                                    what = R.id.GetMessages
                                    arg1 = 1
                                    data = Bundle().also {
                                        it.putParcelableArrayList(
                                            GetMessages::class.java.simpleName,
                                            ary
                                        )
                                    }
                                })
                            } catch (e: Exception) {
                                e.printStackTrace()
                                hlr.sendMessage(hlr.obtainMessage().apply {
                                    what = R.id.GetMessages
                                    arg1 = 2
                                })
                            }
                        }
                    }

            }
        }

        class SendMessage(
            val hlr: MyHandler,
            val sMyId: String,
            val sFriend: String,
            val msgtext: String
        ) : Runnable {
            override fun run() {
                val sRoom = HelpObject.getCompoundRoomName(sMyId, sFriend)

                val db = FirebaseFirestore.getInstance()
                val obj = RoomMessage(msgtext, sMyId)
                val ref = db.collection("rooms").document(sRoom).collection("messages").document()
                db.runBatch { rb ->
                    rb.set(ref, obj)
                    rb.update(ref, "time", FieldValue.serverTimestamp())
                }.addOnSuccessListener {

                    hlr.sendMessage(hlr.obtainMessage().apply {
                        what = R.id.SenMessage
                        arg1 = 1
                    })
                }.addOnFailureListener {
                    it.printStackTrace()

                    hlr.sendMessage(hlr.obtainMessage().apply {
                        what = R.id.SenMessage
                        arg1 = 0
                    })
                }
            }
        }
    }

    lateinit var adaRylv: RylV
    lateinit var mHlr: MyHandler
    lateinit var getMessageObj: GetMessages
    lateinit var adaDatas: ArrayList<RylVItemData>

    lateinit var mMyId: String
    lateinit var mFriendId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatting_activity)

        mProgressBar = ProgressCtrl(progressBar)



        adaDatas = ArrayList<RylVItemData>()

        with(msglist) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            adaRylv = RylV(context, adaDatas)
            adapter = adaRylv
        }


        mHlr = MyHandler(this)

        intent.getStringArrayListExtra(ChattingActivity::class.java.simpleName)?.run {
            mMyId = this[0]
            mFriendId = this[1]
        }


        ib_send_msg.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val txtMsg = edt_msg.text.toString()
                if (txtMsg.isEmpty() == false) {
                    Thread(SendMessage(mHlr, mMyId, mFriendId, txtMsg)).start()
                    edt_msg.setText("")
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()

        mProgressBar.showProgressBar()
        getMessageObj = GetMessages(mHlr, mMyId, mFriendId)
        Thread(getMessageObj).start()

//        doAsync {
//
//            val db=FirebaseFirestore.getInstance()
////            db.collection()
//        }

    }

    override fun onStop() {
        getMessageObj.listening?.remove()
        super.onStop()
    }


}
