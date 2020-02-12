package com.example.myktfirebasestore

import android.content.Context
import android.os.Parcelable
import android.widget.TextView
import androidx.core.text.HtmlCompat
import kotlinx.android.parcel.Parcelize
import java.util.*

enum class Gender{
    M, F, L, G, T, Q, B, Other, Unknown
}

@Parcelize
data class UserInfo(
    var id: String = "", //id這個變數名稱不能隨便換，因為跟string resource的str_uid_tag是綁在一起的
    var name: String = "", var birthday: Date = Date(),
    var gender: Gender = Gender.Unknown, var language: String = "",
    var country: String = "", var desp: String = "",
    var register: Date = Date()
) : Parcelable {
    companion object {
        private val genderMap = hashMapOf<Gender, String>(
            Gender.M to "male",
            Gender.F to "female", Gender.L to "lesbian",
            Gender.Other to "other", Gender.B to "bisexual",
            Gender.Q to "queer", Gender.G to "gay",
            Gender.T to "Tom", Gender.Unknown to ""
        )
    }
    fun outputContent(ctx: Context, tv1: TextView, tv2: TextView) {
        val res = ctx.resources

        val name = res.getString(R.string.userinfo_var_title_name)
        val language = res.getString(R.string.userinfo_var_title_language)
        val country = res.getString(R.string.userinfo_var_title_country)
        val description = res.getString(R.string.userinfo_var_title_description)


        val s =
            "<small>$name:</small><br>${this.name}<p><small>$country:</small><br>${this.country}<p>" +
                    "<small>$language:</small><br>${this.language}<p>" +
                    "<small>gender:</small><br>${genderMap[gender]}"
        tv1.setText(HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE))

        val s2 = "<small>$description:</small>\n${this.desp}"
        tv2.setText(HtmlCompat.fromHtml(s2, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE))
    }

}


data class TravelInfo(var cityFrom:String, var cityTo:String, var willGo:Date, var willCome:Date, var info:String)
