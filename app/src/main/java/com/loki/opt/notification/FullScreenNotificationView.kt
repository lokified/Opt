package com.loki.opt.notification

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.loki.opt.R
import com.loki.opt.util.stopNotifService
import timber.log.Timber

class FullScreenNotificationView(
    private val context: Context
) {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var layoutInflater: LayoutInflater? = null
    private var params: WindowManager.LayoutParams? = null

    init {
        showOverlay()
    }

    private fun showOverlay() {
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = layoutInflater?.inflate(R.layout.full_screen_notification_layout, null)

        overlayView?.findViewById<ImageView>(R.id.btn_close)?.setOnClickListener {
            removeOverLay()
        }

        params?.gravity = Gravity.START or Gravity.TOP
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun open() {
        try {
            if (overlayView?.windowToken == null) {
                windowManager?.addView(overlayView, params)
            }
        } catch (e: Exception) {
            Timber.d("window open", e.toString())
        }
    }

    fun removeOverLay() {
        try {
            overlayView?.let {
                windowManager?.removeView(it)
                overlayView = null
                params = null
                context.stopNotifService()
            }
        } catch (e: Exception) {
            Timber.d("window close", e.toString())
        }
    }
}