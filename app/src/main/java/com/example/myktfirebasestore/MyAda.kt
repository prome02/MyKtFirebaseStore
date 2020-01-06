package com.example.myktfirebasestore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAda:RecyclerView.Adapter<MyAda.VH>() {
    val mLst=ArrayList<String>()
    init {
        for (i in 1..15){
            mLst.add("str $i")
        }
    }
    class VH(v:View):RecyclerView.ViewHolder(v){

        var tvXXX: TextView
        var btnRemove: Button
        init {
            tvXXX=v.findViewById<TextView>(R.id.textViewxxx)
            btnRemove=v.findViewById<Button>(R.id.btn_remove)
        }

    }


    override fun getItemCount()=mLst.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.lyt_dlg_itemview, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.apply {
            tvXXX.text=mLst[position]
            btnRemove.tag=holder
            btnRemove.setOnClickListener(object:View.OnClickListener{
                override fun onClick(p0: View?) {

                        mLst.removeAt(layoutPosition)
                        notifyItemRemoved(layoutPosition)



                }
            })
        }


    }
}