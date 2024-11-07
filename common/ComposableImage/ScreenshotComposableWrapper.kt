package com.example.bamboogarden.common.ComposableImage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

@SuppressLint("ViewConstructor")
class ScreenshotComposableWrapper(
  context: Context,
  onBitmapCreated: (bitmap: Bitmap) -> Unit,
  component: @Composable () -> Unit,
) : LinearLayoutCompat(context) {

  private var observer: ViewTreeObserver.OnGlobalLayoutListener

  init {
    val width = 600
    val height = 50
    val view = ComposeView(context)

    view.visibility = View.GONE
    view.layoutParams = LayoutParams(width, height)
    this.addView(view)

    view.setContent {
      component()
    }

    observer = ViewTreeObserver.OnGlobalLayoutListener {
      val graphicUtils = GraphicUtils()
      val bitmap = graphicUtils.createBitmapFromView(view = view, width = width, height = height)
      onBitmapCreated(bitmap)
    }

    viewTreeObserver.addOnGlobalLayoutListener(observer)
  }

  override fun onDetachedFromWindow() {
    viewTreeObserver.removeOnGlobalLayoutListener(observer)
    super.onDetachedFromWindow()
  }

}