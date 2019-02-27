package com.nooblabs.thermalconfigchanger.extensions

import android.view.View

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun hideAll(vararg views: View) {
    views.forEach { it.hide() }
}

fun showAll(vararg views: View) {
    views.forEach { it.show() }
}

fun toggleState(isEnabled: Boolean, vararg views: View) {
    views.forEach { it.isEnabled = isEnabled}
}