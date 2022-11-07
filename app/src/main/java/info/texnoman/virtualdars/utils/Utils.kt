package info.texnoman.virtualdars.utils

import androidx.core.app.ActivityCompat
import info.texnoman.virtualdars.activity.VideoDownloadActivity
import permissions.dispatcher.PermissionUtils
import java.util.*

class Utils {
    companion object {
        const val BASE_URL = "https://onlinedars.uz/"
    }
}

private val units = arrayOf("B", "KB", "MB", "GB", "TB")
/**
 * 单位转换
 */
fun getSizeUnit(size: Double): String {
    var sizeUnit = size
    var index = 0
    while (sizeUnit > 1024 && index < 4) {
        sizeUnit /= 1024.0
        index++
    }
    return String.format(Locale.getDefault(), "%.2f %s", sizeUnit, units[index])
}

/*
private const val REQUEST_INITLIST: Int = 0

private val PERMISSION_INITLIST: Array<String> = arrayOf("android.permission.READ_EXTERNAL_STORAGE",
    "android.permission.WRITE_EXTERNAL_STORAGE")

fun VideoDownloadActivity.initListWithPermissionCheck() {
    if (PermissionUtils.hasSelfPermissions(this, *PERMISSION_INITLIST)) {
        initList()
    } else {
        ActivityCompat.requestPermissions(this, PERMISSION_INITLIST, REQUEST_INITLIST)
    }
}
fun VideoDownloadActivity.onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
    when (requestCode) {
        REQUEST_INITLIST ->
        {
            if (PermissionUtils.verifyPermissions(*grantResults)) {
                initList()
            } else {
                onDenied()
            }
        }
    }
}*/
