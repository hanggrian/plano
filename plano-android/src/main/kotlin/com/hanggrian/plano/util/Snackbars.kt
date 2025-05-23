package com.hanggrian.plano.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

inline fun View.snackbar(message: String) =
    Snackbar
        .make(this, message, Snackbar.LENGTH_SHORT)
        .show()

inline fun View.snackbar(message: String, actionText: String, noinline action: (View) -> Unit) =
    Snackbar
        .make(this, message, Snackbar.LENGTH_SHORT)
        .setAction(actionText, action)
        .show()

inline fun View.longSnackbar(message: String) =
    Snackbar
        .make(this, message, Snackbar.LENGTH_LONG)
        .show()

inline fun View.longSnackbar(message: String, actionText: String, noinline action: (View) -> Unit) =
    Snackbar
        .make(this, message, Snackbar.LENGTH_LONG)
        .setAction(actionText, action)
        .show()
