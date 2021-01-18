package tv.gamesee.utils.helper

import android.Manifest
import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.app.perk99driver.BuildConfig

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by root on 27/2/19.
 */
abstract class ImagePickerLatest : AppCompatActivity() {
    private val SELECT_FILE = 200
    private val REQUEST_CAMERA = 201
    private val REQUEST_PERMISSIONS_CAMERA = 2
    lateinit var mImageFile: File
    private var image_path = ""

    fun checkPermissionCamera() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!cameraPermission(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), REQUEST_PERMISSIONS_CAMERA
                )
                return
            } else {
                getImage()
            }
        } else {
            getImage()
        }
    }


    private fun getImage() {


        val items =
            arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle("Choose Your Option")
        builder.setItems(items) { dialog, item -> //  boolean result= Utility.checkPermission(ShareDeal.this);
            if (items[item] == "Take Photo") {
                cameraIntent()
            } else if (items[item] == "Choose from Library") {
                galleryIntent()
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()












   /*     val dialog = Dialog(this, R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(tv.gamesee.R.layout.dialog_select_image)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.layCamera.setOnClickListener {
            dialog.dismiss()
            cameraIntent()
        }
        dialog.layGallery.setOnClickListener {
            dialog.dismiss()
            galleryIntent()
        }
        dialog.show()*/
    }


    override fun onStart() {
        super.onStart()
        Log.e("onStart", "here")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissionCheck = PackageManager.PERMISSION_GRANTED
        for (permission in grantResults) {
            permissionCheck = permissionCheck + permission
        }
        if (grantResults.size > 0 && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getImage()
        }
    }

    fun cameraPermission(permissions: Array<String>): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permissions[0]
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            permissions[1]
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            permissions[2]
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun cameraIntent() {

        try {
            createImageFile(this)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file_uri =
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", mImageFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file_uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    //TODO open gallery
    private fun galleryIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECT_FILE)
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        mImageFile = File.createTempFile(
            imageFileName,
            ".png",
            storageDir
        )
        return mImageFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                val uri = Uri.fromFile(mImageFile)
                var picture_path = getAbsolutePath(this, uri)
                selectedImage(picture_path)
            } else if (requestCode == SELECT_FILE) {
                val uri = data?.getData()
//                var picture_path = UtilJava.getImagePathFromInputStreamUri(this, uri!!)
                var picture_path = getImagePathFromInputStreamUri(this, uri!!)
//                var picture_path = CommonMethods.getAbsolutePath(this, uri!!)
                selectedImage(picture_path)
            }
        }
    }

    fun getAbsolutePath(activity: Context, uri: Uri): String {

        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf("_data")
            var cursor: Cursor? = null
            try {
                cursor = activity.contentResolver.query(uri, projection, null, null, null)
                val column_index = cursor!!.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
                // Eat it
                e.printStackTrace()
            }

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path!!
        }
        return ""
    }


    fun getImagePathFromInputStreamUri(context: Context, uri: Uri): String? {
        var inputStream: InputStream? = null
        var filePath: String? = null

        if (uri.authority != null) {
            try {
                inputStream = context.contentResolver.openInputStream(uri) // context needed
                val photoFile = createTemporalFileFrom(inputStream, context)

                filePath = photoFile!!.path

            } catch (e: FileNotFoundException) {
                // log
            } catch (e: IOException) {
                // log
            } finally {
                try {
                    inputStream!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        return filePath
    }

    @Throws(IOException::class)
    fun createTemporalFileFrom(inputStream: InputStream?, context: Context): File? {
        var targetFile: File? = null

        if (inputStream != null) {
            var read: Int
            val buffer = ByteArray(8 * 1024)

            targetFile = createImageFile(context)

//            val outputStream = FileOutputStream(targetFile)
//            read = inputStream.read(buffer)
//            inputStream.copyTo(outputStream, read)
//            while (read != -1) {
//                outputStream.write(buffer, 0, read)
//            }
//            outputStream.flush()
            inputStream.toFile(targetFile.path)
//            targetFile.copyInputStreamToFile(inputStream)

            try {
//                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return targetFile
    }

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }


    private fun InputStream.toFile(path: String) {
        use { input ->
            File(path).outputStream().use { input.copyTo(it) }
        }
    }

    fun createTemporalFile(context: Context): File {
        return File(
            context.externalCacheDir,
            System.currentTimeMillis().toString() + ".jpg"
        ) // context needed
    }

    abstract fun selectedImage(imagePath: String?)
}