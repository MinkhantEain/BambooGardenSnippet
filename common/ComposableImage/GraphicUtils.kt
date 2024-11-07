package com.example.bamboogarden.common.ComposableImage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat

class GraphicUtils {
  fun createBitmapFromView(view: View, width: Int, height: Int): Bitmap {
    view.layoutParams = LinearLayoutCompat.LayoutParams(
      LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
      LinearLayoutCompat.LayoutParams.WRAP_CONTENT
    )

    view.measure(
      View.MeasureSpec.makeMeasureSpec(1200, View.MeasureSpec.AT_MOST),
      View.MeasureSpec.makeMeasureSpec(4000, View.MeasureSpec.AT_MOST)
    )

    view.layout(0, 0, view.measuredWidth, view.measuredHeight)

    val canvas = Canvas()
    val bitmap = Bitmap.createBitmap(view.measuredWidth, view.height, Bitmap.Config.ARGB_8888)

    canvas.setBitmap(bitmap)
    view.draw(canvas)

    return bitmap
  }
}