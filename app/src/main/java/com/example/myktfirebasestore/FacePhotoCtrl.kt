package com.example.myktfirebasestore

import android.R
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import java.io.File
import java.util.ArrayList

class CropOption {
    var title: CharSequence? = null
    var icon: Drawable? = null
    var appIntent: Intent? = null
}

class FacePhotoCtrl(val act: Activity, val ivFace: ImageView) {
    private val PICK_FROM_CAMERA = 1
    private val CROP_FROM_CAMERA = 2
    private val PICK_FROM_FILE = 3
    var mImageCaptureUri: Uri? = null

    fun send2Act(aryItemSelections: Array<String>, ridItemSelectionsTitle: Int) {

        val items = aryItemSelections
        val adapter = ArrayAdapter<String>(act, R.layout.select_dialog_item, items)
        val builder = AlertDialog.Builder(act)

        val resource = act.resources

        builder.setTitle(resource.getString(ridItemSelectionsTitle))
            .setAdapter(adapter, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                    if (which == 0) {
                        val it = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        val fPath = "tmp_avatar_${System.currentTimeMillis()}.jpg"
                        val parentPath = Environment.getExternalStorageDirectory().absolutePath
                        val dPath = File("$parentPath/$fPath")
                        mImageCaptureUri = Uri.fromFile(dPath)
                        it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri)

                        try {
                            it.putExtra("return-data", true)

                            act.startActivityForResult(it, PICK_FROM_CAMERA)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        val it = Intent().apply {
                            setType("image/*")
                            action = Intent.ACTION_GET_CONTENT
                        }

                        act.startActivityForResult(
                            Intent.createChooser(
                                it,
                                "Complete action using"
                            ), PICK_FROM_FILE
                        )

                    }
                }
            })
            .create().show()
    }

    private fun doCrop() {
//        val cropOptions: ArrayList<CropOption> = ArrayList<CropOption>()
        val intent = Intent("com.android.camera.action.CROP")
        intent.type = "image/*"
        val list =
            act.packageManager.queryIntentActivities(intent, 0)
        val size = list.size
        if (size == 0) {
            Toast.makeText(act, "Can not find image crop app", Toast.LENGTH_SHORT).show()
            return
        } else {
            intent.data = mImageCaptureUri
            intent.putExtra("outputX", 200)
            intent.putExtra("outputY", 200)
            intent.putExtra("aspectX", 1)
            intent.putExtra("aspectY", 1)
            intent.putExtra("scale", true)
            intent.putExtra("return-data", true)
            if (size == 1) {
                val i = intent//Intent(intent)
                val res = list[0]
                i.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                act.startActivityForResult(i, CROP_FROM_CAMERA)
            } else {
//                for (res in list) {
//                    val co = CropOption()
//                    co.title =
//                        packageManager.getApplicationLabel(res.activityInfo.applicationInfo)
//                    co.icon =
//                        packageManager.getApplicationIcon(res.activityInfo.applicationInfo)
//                    co.appIntent = Intent(intent)
//                    co.appIntent?.setComponent(
//                        ComponentName(
//                            res.activityInfo.packageName,
//                            res.activityInfo.name
//                        )
//                    )
//                    cropOptions.add(co)
//                }
//                val adapter =
//                    CropOptionAdapter(applicationContext, cropOptions)
//                val builder = android.app.AlertDialog.Builder(this)
//                builder.setTitle("Choose Crop App")
//                builder.setAdapter(
//                    adapter
//                ) { dialog, item ->
//                    startActivityForResult(
//                        cropOptions[item].appIntent, CROP_FROM_CAMERA
//                    )
//                }
//                builder.setOnCancelListener {
//                    if (mImageCaptureUri != null) {
//                        contentResolver.delete(mImageCaptureUri?:return@setOnCancelListener,
//                            null, null)
//                        mImageCaptureUri = null
//                    }
//                }
//                val alert = builder.create()
//                alert.show()
            }
        }
    }

    fun setImageByBM(bm: Bitmap) {

        ivFace.setImageBitmap(bm)


    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICK_FROM_CAMERA -> doCrop()
            PICK_FROM_FILE -> {
                data ?: return
                mImageCaptureUri = data.data!!
                doCrop()
            }
            CROP_FROM_CAMERA -> {
                try {
                    val extras = data!!.extras
                    if (extras != null) {
                        val photo = extras.getParcelable<Bitmap>("data")
//                        mImageView.setImageBitmap(photo)
                        setImageByBM(photo!!)
                    }
                    val f = File(mImageCaptureUri?.path!!)
                    if (f.exists()) f.delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }


//                ReadBmpFromUri().execute(mImageCaptureUri)

//                try {
//                    when(mWhereImageFrom){
//                        0->{
//                            ReadBmpFromFile().execute(mImageCaptureUri?.toFile()?.path)
//                        }
//                        1->{
//                            ReadBmpFromUri().execute(mImageCaptureUri)
//                        }
//                    }
//
//                } catch (e: Exception) {
//                }
            }
        }
    }

}