package com.leomarkpaway.formvalidation.util

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun Context.showDialog(
    title: String = "Alert",
    message: String,
    positiveButtonText: String = "OK",
    negativeButtonText: String? = null,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null
) {
    val builder = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonText) { _, _ -> onPositiveClick?.invoke() }

    negativeButtonText?.let {
        builder.setNegativeButton(it) { _, _ -> onNegativeClick?.invoke() }
    }

    builder.create().show()
}