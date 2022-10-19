package com.example.yantram.Utilities


import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Base64
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.SphericalUtil
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.reflect.Field
import java.net.URISyntaxException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.*


class Utils {

    private var msg: Toast? = null
    internal var locationManager: LocationManager? = null
    internal var context: Context? = null

    lateinit var date: String
    var rememberMeSharedpreferences: SharedPreferences? = null


    fun hideKeyboard(ctx: Context) {
        val inputManager = ctx
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = (ctx as Activity).currentFocus ?: return
        inputManager.hideSoftInputFromWindow(v.windowToken, 0)
    }


    fun showKeyboard(context: Context) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
            InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }


    fun showSoftKeyboard(view: View, context: Context) {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        inputMethodManager.showSoftInput(view, 0)
    }


    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }


    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }


    fun getDisplayHieght(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getDisplayWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }


    fun setPlayerIdOneSignnal() {

    }

    fun getScreenHieght(context: Context?): Int {
        return context!!.resources.displayMetrics.heightPixels
    }

    @Synchronized
    fun showToast(context: Context?, toast: String): Toast {
        if (context != null && msg == null || msg!!.view!!.windowVisibility != View.VISIBLE) {
            msg = Toast.makeText(context, toast, Toast.LENGTH_LONG)
            msg!!.setGravity(Gravity.CENTER, 0, 0)
            msg!!.show()
        }
        return msg as Toast
    }


    fun isValidMobile(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }


    fun formatDate(dateStr: String?): String? {
        if (dateStr == null) return null
        var date = "Format Exception"
        val currentFormat = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
        val requiredFormat = SimpleDateFormat("mm-dd-yyyy", Locale.getDefault())
        try {
            val getDate = currentFormat.parse(dateStr)
            date = requiredFormat.format(getDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    fun reFormatDate(date: String): String {
        //  val date:String= this!!.formatDate(date)!!
        var input: String = date

        var out = input.split("/")
        System.out.println("Year = " + out[0]);

        val yearValue: String = out[0]

        val yearLastValue: String = yearValue.takeLast(2)
        System.out.println("Month = " + out[1]);

        val month: String = out[1]

        System.out.println("Day = " + out[2]);
        val day: String = out[2]

        val stringCombine = yearLastValue.plus(month).plus(day)
        return stringCombine

    }

    fun isValidPassword(password: String): Boolean {
        var hasMinimum6Chars = Regex("^[a-zA-Z0-9]{6,}+$");
        var isValidated = hasMinimum6Chars.matches(password);
        return isValidated
    }

    companion object {

        var MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
        var MY_PERMISSIONS_REQUEST_ACCESS_LOCATIONN = 124
        private var utilities: Utils? = null


        val instance: Utils
            get() {
                if (utilities == null) {
                    utilities = Utils()
                }
                return utilities!!
            }
    }


    private fun isValidMobile(nation: String, mobile: String): Boolean {
        if (nation.equals("202")) {
            return mobile.trim().length == 10
        } else {
            return true
        }
    }

    public fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun getFieldValue(field: Field, target: Any): Any {
        field.isAccessible = true

        if (field.get(target) != null) {
            return field.get(target)
        }
        return ""
    }

    fun getSpannableText(text: String, start: Int, end: Int): SpannableString {
        val ss = SpannableString(text)
        ss.setSpan(RelativeSizeSpan(.75f), start, end, 0) // set size
        return ss
    }

    fun getSpannableTextColor(text: String, start: Int, end: Int): SpannableString {
        val ss = SpannableString(text)
        ss.setSpan(
            ForegroundColorSpan(Color.RED),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        ) // set size
        return ss
    }


    @Throws(IOException::class)
    fun betweenDates(firstDate: Date, secondDate: Date): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ChronoUnit.DAYS.between(firstDate.toInstant(), secondDate.toInstant())
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }


    var getLanguageCode: Map<Int, String> = object : HashMap<Int, String>() {
        init {
            put(0, "en")
            put(1, "ru")

        }
    }

    @SuppressLint("CommitPrefEdits")
    fun rememberMeSave(context: Context, key: String, value: String) {

        context.getSharedPreferences("new", MODE_PRIVATE)
            .edit()
            .putString(key, value)

            .apply();

    }

    fun rememberMeGet(context: Context, key: String): String {
        val pref: SharedPreferences = context.getSharedPreferences("new", MODE_PRIVATE)
        val name = pref.getString(key, null)

        return name!!
    }


    @Synchronized
    fun showDropDownDialog(
        context: Context,
        array: ArrayList<String>,
        onItemClick: (value: Boolean, pos: Int) -> Unit
    ) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Select")


        builder.setItems(array.toTypedArray(), DialogInterface.OnClickListener { dialog, item ->
            try {
                onItemClick(true, item)
                dialog.dismiss()
            } catch (e: Exception) {
                dialog.dismiss()
            }

        })
        builder.show()
    }


    @Synchronized
    fun showCountryDialog(context: Context, array: Array<String>) {
        val builder = android.app.AlertDialog.Builder(context)
        //    builder.setTitle("Select")
        builder.setItems(
            Adapter.IGNORE_ITEM_VIEW_TYPE,
            DialogInterface.OnClickListener { dialog, item ->

                dialog.dismiss()

            })
        builder.show()
    }


    fun BitmapToBase64(bitmap: Bitmap?): String? {
        val encodedImage: String
        val baos = ByteArrayOutputStream() //
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) //bm is the bitmap object
            val b = baos.toByteArray()
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
            return encodedImage
        }
        return ""
    }


    fun getRealPathFromURI(uri: Uri?, activity: Activity): String? {
        var path = ""
        if (activity.getContentResolver() != null) {
            val cursor: Cursor =
                activity.getContentResolver().query(uri!!, null, null, null, null)!!
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }


    @Synchronized
    fun showDaySlotDialog(
        tv_slotday: EditText,
        context: Context,
        array: ArrayList<String>, onItemClick: (pos: Int) -> Unit
    ) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Select")
        builder.setItems(array.toTypedArray(), DialogInterface.OnClickListener { dialog, item ->
            try {
                onItemClick(item)
                tv_slotday.setText(array.get(item).toString())
                dialog.dismiss()
            } catch (e: Exception) {

                dialog.dismiss()

            }


        })
        builder.show()
    }


    fun getRandomNumberString(): String? {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        val rnd = Random()
        val number = rnd.nextInt(999999)

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number)
    }



    @SuppressLint("NewApi")
    @Throws(URISyntaxException::class)
    fun getFilePath(context: Context, uri: Uri): String? {
        var uri = uri
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(
                context.applicationContext,
                uri
            )
        ) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("image" == type) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(
                    split[1]
                )
            }
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf(
                MediaStore.Images.Media.DATA
            )
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver
                    .query(uri, projection, selection, selectionArgs, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun getDistanceOfList(currentToDestList: List<LatLng>) : Double{
       var totalDistance = 0.0;

        for (i in 0 until currentToDestList.size-1){
                totalDistance += SphericalUtil.computeDistanceBetween(LatLng(currentToDestList[i].latitude, currentToDestList[i].longitude),
                    LatLng(currentToDestList[i + 1].latitude, currentToDestList[i + 1].longitude))
       }

        return totalDistance;
    }

    fun calculateDistance(lat1:Double, lon1:Double, lat2:Double, lon2:Double): Double{
        return SphericalUtil.computeDistanceBetween(LatLng(lat1, lon1), LatLng(lat2, lon2));
    }

    fun convertMeterToKilometer(meter: Double): String? {
        return removeDecimalZeroFormatTwo(meter * 0.001)
    }

    private fun removeDecimalZeroFormatTwo(n: Double): String {
        val number2digits:Double = String.format("%.2f", n).toDouble()
        return  number2digits.toString()
    }

}







