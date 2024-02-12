package com.hocel.chirrup.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.text.SimpleDateFormat
import java.util.*

fun String.toast(context: Context, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, this, length).show()

fun convertTimeStampToDateAndTime(epoch: Long): String {
    val date = Date(epoch)
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm a", Locale.US)
    return sdf.format(date)
}

fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", text)
    clipboardManager.setPrimaryClip(clipData)
    "Copied to clipboard".toast(context, Toast.LENGTH_SHORT)
}

fun downloadFile(
    mContext: Context,
    fileName: String,
    fileExtension: String,
    destinationDirectory: String,
    uri: Uri
) {
    val downloadManager: DownloadManager =
        mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(uri)
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(
        destinationDirectory,
        fileName + fileExtension
    )
    val downloadId = downloadManager.enqueue(request)
    val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                "Download is complete".toast(mContext, Toast.LENGTH_SHORT)
            }
        }
    }
    LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, filter)
}