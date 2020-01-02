package com.example.myktfirebasestore

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import java.lang.Exception

class HelpObject(val ctx: Context, val titleMsgId:Int, val contentId:Int): View.OnClickListener {
    override fun onClick(p0: View?) {

        showDlg(ctx, titleMsgId, contentId)
    }

    companion object {
//        fun getUserId(ct: Context): String? {
//            val res = ct.resources
//            val pref =
//                ct.getSharedPreferences(res.getString(R.string.str_pref_name), Context.MODE_PRIVATE)
//            if (pref == null) return null
//
//            val tagEmailID = res.getString(R.string.str_uid_tag)
//            val str = pref.getString(tagEmailID, "")
//            if (str == null) return null
//            if(str.length==0) return ""
//
//            return pref.getString(res.getString(R.string.app_name),"")
//
//        }

        fun showDlg(ct:Context, title:String, content:String){
            AlertDialog.Builder(ct)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(android.R.string.ok, object :DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }
                }).show()
        }
    fun showDlg(ct:Context, strTitle:Int, strCnt:Int) {
        AlertDialog.Builder(ct)
            .setTitle(ct.resources.getString(strTitle))
            .setMessage(ct.resources.getString(strCnt))
            .setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }
            }).show()
    }
}
}