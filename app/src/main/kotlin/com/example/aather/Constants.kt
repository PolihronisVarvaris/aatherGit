package com.example.aather

import android.Manifest

object Constants {

    const val Tag="camerax"
    const val FILE_NAME_FORMAT="yy-MM-dd-HH-mm-ss-SSS"
    const val REQUEST_CODE_PERMISSIONS = 123
    val REQUIRED_PERMISSIONS= arrayOf(Manifest.permission.CAMERA)
}