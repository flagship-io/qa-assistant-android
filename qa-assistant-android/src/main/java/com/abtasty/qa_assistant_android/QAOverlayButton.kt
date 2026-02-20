package com.abtasty.qa_assistant_android

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.navigation.compose.rememberNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.ref.WeakReference

@SuppressLint("ClickableViewAccessibility")
class QAOverlayButton(application: Application) {

    private val mainHandler = Handler(Looper.getMainLooper())
    internal var currentActivity: WeakReference<Activity>? = null
    private val overlayViewTag = "qa_overlay_button"

    private val prefs: SharedPreferences =
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private var portraitRatioX = prefs.getFloat(KEY_PORTRAIT_RATIO_X, -1f)
    private var portraitRatioY = prefs.getFloat(KEY_PORTRAIT_RATIO_Y, -1f)
    private var landscapeRatioX = prefs.getFloat(KEY_LANDSCAPE_RATIO_X, -1f)
    private var landscapeRatioY = prefs.getFloat(KEY_LANDSCAPE_RATIO_Y, -1f)

    private var dialogDisplayed = false

    private val lifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            currentActivity = WeakReference(activity)
            mainHandler.post { attachToActivity(activity) }
        }

        override fun onActivityPaused(activity: Activity) {
            savePositionFromActivity(activity)
            detachFromActivity(activity)
            if (currentActivity?.get() === activity) {
                currentActivity = null
            }
            if (dialogDisplayed) {
                dialogDisplayed = false
            }
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}

        //Mettre le lifecycleCallback dans le singleton
        //Faire la vue comme le button
        //Dialoguer entre LC et vues
    }

    private val appRef = WeakReference(application)

    init {
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    fun dismiss() {
        currentActivity?.get()?.let {
            savePositionFromActivity(it)
            detachFromActivity(it)
        }
        currentActivity = null
        appRef.get()?.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    private fun isPortrait(activity: Activity): Boolean {
        return activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    private fun savePositionFromView(view: View, activity: Activity) {
        val params = view.layoutParams as? FrameLayout.LayoutParams ?: return
        val parent = view.parent as? ViewGroup ?: return

        val maxLeft = parent.width - view.width
        val maxTop = parent.height - view.height
        if (maxLeft <= 0 || maxTop <= 0) return

        val ratioX = params.leftMargin.toFloat() / maxLeft
        val ratioY = params.topMargin.toFloat() / maxTop

        if (isPortrait(activity)) {
            portraitRatioX = ratioX
            portraitRatioY = ratioY
            prefs.edit(commit = true) {
                putFloat(KEY_PORTRAIT_RATIO_X, ratioX)
                putFloat(KEY_PORTRAIT_RATIO_Y, ratioY)
            }
        } else {
            landscapeRatioX = ratioX
            landscapeRatioY = ratioY
            prefs.edit(commit = true) {
                putFloat(KEY_LANDSCAPE_RATIO_X, ratioX)
                putFloat(KEY_LANDSCAPE_RATIO_Y, ratioY)
            }
        }
    }

    private fun savePositionFromActivity(activity: Activity) {
        val decorView = activity.window.decorView as? ViewGroup ?: return
        val button = decorView.findViewWithTag<View>(overlayViewTag) ?: return
        savePositionFromView(button, activity)
    }

    private fun attachToActivity(activity: Activity) {
        val decorView = activity.window.decorView as? ViewGroup ?: return
        if (decorView.findViewWithTag<View>(overlayViewTag) != null) return

        val density = activity.resources.displayMetrics.density
        val sizePx = (56 * density).toInt()

        val button = createButtonView(activity, density, sizePx)
        button.tag = overlayViewTag

        val params = FrameLayout.LayoutParams(sizePx, sizePx).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        setupDragBehavior(button, activity)
        decorView.addView(button, params)

        button.post {
            val parent = button.parent as? ViewGroup ?: return@post
            val lp = button.layoutParams as FrameLayout.LayoutParams
            val maxLeft = parent.width - button.width
            val maxTop = parent.height - button.height
            if (maxLeft <= 0 || maxTop <= 0) return@post

            val portrait = isPortrait(activity)
            val savedRatioX = if (portrait) portraitRatioX else landscapeRatioX
            val savedRatioY = if (portrait) portraitRatioY else landscapeRatioY

            if (savedRatioX in 0f..1f && savedRatioY in 0f..1f) {
                lp.leftMargin = (savedRatioX * maxLeft).toInt().coerceIn(0, maxLeft)
                lp.topMargin = (savedRatioY * maxTop).toInt().coerceIn(0, maxTop)
            } else {
                val margin = (16 * density).toInt()
                lp.leftMargin = (parent.width - sizePx - margin).coerceIn(0, maxLeft)
                lp.topMargin = (parent.height - sizePx - margin).coerceIn(0, maxTop)
            }
            button.layoutParams = lp
        }
    }

    private fun detachFromActivity(activity: Activity) {
        val decorView = activity.window.decorView as? ViewGroup ?: return
        decorView.findViewWithTag<View>(overlayViewTag)?.let {
            decorView.removeView(it)
        }
    }

    private fun createButtonView(context: Context, density: Float, sizePx: Int): View {
//        val background = GradientDrawable().apply {
//            shape = GradientDrawable.ROU
////            setColor(Color.parseColor("#1E88E5"))
//        }
//
//        return FloatingActionButton(context).apply {
////            this.setImageResource(R.drawable.icon)
//            backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
//            rippleColor = Color.TRANSPARENT
//            this.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon))
////            text = "QA"
////            setTextColor(Color.WHITE)
////            textSize = 16
////            gravity = Gravity.CENTER
////            this.background = background
//            imageTintList = null
//            setPadding(0, 0, 0, 0)
//            isClickable = true
//            isFocusable = true
//            elevation = 8 * density
//            layoutParams = FrameLayout.LayoutParams(sizePx, sizePx)
//            setOnClickListener {
//                println("000000000")
//                val activity = currentActivity?.get() ?: (context as? Activity)
//                if (activity != null && !activity.isFinishing) {
//                    showQABottomSheet(activity)
//                }
//            }
//        }

        return AppCompatImageView(context).apply {

            layoutParams = FrameLayout.LayoutParams(sizePx, sizePx)

            setImageResource(R.drawable.icon_selector)

            scaleType = ImageView.ScaleType.FIT_CENTER

            isClickable = true
            isFocusable = true

            ViewCompat.setElevation(this, 8 * density)

            setOnClickListener {
                val activity = currentActivity?.get() ?: (context as? Activity)
                if (activity != null && !activity.isFinishing) {
                    showQABottomSheet(activity)
                }
            }
        }
    }

    private fun setupDragBehavior(view: View, activity: Activity) {
        var downRawX = 0f
        var downRawY = 0f
        var downMarginLeft = 0
        var downMarginTop = 0

        view.setOnTouchListener { v, event ->
            val params = v.layoutParams as FrameLayout.LayoutParams
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downRawX = event.rawX
                    downRawY = event.rawY
                    downMarginLeft = params.leftMargin
                    downMarginTop = params.topMargin
                    false
                }

                MotionEvent.ACTION_MOVE -> {
                    val parent = v.parent as? ViewGroup ?: return@setOnTouchListener false
                    val maxLeft = parent.width - v.width
                    val maxTop = parent.height - v.height

                    val newLeft = downMarginLeft + (event.rawX - downRawX).toInt()
                    val newTop = downMarginTop + (event.rawY - downRawY).toInt()

                    params.leftMargin = newLeft.coerceIn(0, maxLeft)
                    params.topMargin = newTop.coerceIn(0, maxTop)
                    v.layoutParams = params
                    false
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    savePositionFromView(v, activity)
                    false
                }
                else -> false
            }
        }
    }

    private fun showQABottomSheet(activity: Activity) {
        if (!dialogDisplayed) {
            println("HEREEEE ")
            val dialog = QAADialog(activity, onClose = {
                dialogDisplayed = false
            })

            dialog.setOnCancelListener { dialogDisplayed = false }
            dialog.setOnDismissListener { dialogDisplayed = false }
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialog.behavior.skipCollapsed = true

            dialog.setOnShowListener {
                val bottomSheet =
                    dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
            }

            dialog.show()
            dialogDisplayed = true
        }
    }

//    @Composable
//    private fun QASheetContent(onClose: () -> Unit) {
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .clip(
//                    RoundedCornerShape(
//                        topStart = 24.dp,
//                        topEnd = 24.dp,
//                        bottomStart = 24.dp,
//                        bottomEnd = 24.dp
//                    )
//                )
//                .background(color = colorResource(R.color.primary_background))
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(8.dp)
//            ) {
//                Spacer(Modifier.height(8.dp))
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(4.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    Box(contentAlignment = Alignment.CenterStart)  {
//                        val interactionSource = remember { MutableInteractionSource() }
//                        val isPressed = interactionSource.collectIsPressedAsState().value
//
//                        val tintColor = if (isPressed) {
//                            colorResource(R.color.back_arrow_pressed)
//                        } else {
//                            colorResource(R.color.back_arrow_normal)
//                        }
//
//                        IconButton(
//                            onClick = {},
//                            interactionSource = interactionSource
//                        ) {
//                            Icon(
//                                imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
//                                contentDescription = null,
//                                tint = tintColor
//                            )
//                        }
//                    }
//                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
//                        Image(
//                            imageVector = ImageVector.vectorResource(R.drawable.icon),
//                            contentDescription = "",
//                            modifier = Modifier.padding()
//                        )
//                        Spacer(Modifier.width(8.dp))
//                        Text(
//                            "QA Assistant",
//                            fontFamily = FontFamily.SansSerif,
//                            fontSize = 24.sp,
//                            fontWeight = FontWeight.Normal
//                        )
//                    }
//                    Box(contentAlignment = Alignment.CenterEnd) {
//                        val interactionSource = remember { MutableInteractionSource() }
//                        val isPressed = interactionSource.collectIsPressedAsState().value
//
//                        val tintColor = if (isPressed) {
//                            colorResource(R.color.close_pressed)
//                        } else {
//                            colorResource(R.color.close_normal)
//                        }
//
//                        IconButton(
//                            onClick = {},
//                            interactionSource = interactionSource
//                        ) {
//                            Icon(
//                                imageVector = ImageVector.vectorResource(R.drawable.close),
//                                contentDescription = null,
//                                tint = tintColor
//                            )
//                        }
//                    }
//                }
////                Text("QA Assistant", style = MaterialTheme.typography.titleLarge)
//                Spacer(Modifier.height(16.dp))
//
//                Button(onClick = { /* TODO actions */ }, modifier = Modifier.fillMaxWidth()) {
//                    Text("Action 1")
//                }
//
//                Spacer(Modifier.height(12.dp))
//
//                OutlinedButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
//                    Text("Fermer")
//                }
//            }
//        }
//    }

    companion object {
        private const val PREFS_NAME = "qa_overlay_button_prefs"
        private const val KEY_PORTRAIT_RATIO_X = "portrait_ratio_x"
        private const val KEY_PORTRAIT_RATIO_Y = "portrait_ratio_y"
        private const val KEY_LANDSCAPE_RATIO_X = "landscape_ratio_x"
        private const val KEY_LANDSCAPE_RATIO_Y = "landscape_ratio_y"

        fun create(context: Context): QAOverlayButton? {
            val app = context.applicationContext as? Application ?: return null
            val button = QAOverlayButton(app)
            val activity = context as? Activity
            activity?.let {
                button.currentActivity = WeakReference(it)
                button.mainHandler.post { button.attachToActivity(it) }
            }
            return button
        }
    }
}
