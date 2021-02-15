package cd.shuri.smaprtpay.merchant.screens.ticket

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class RectOverlay(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    private val rectBounds: MutableList<RectF> = mutableListOf()
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context!!, android.R.color.white)
        strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Pass it a list of RectF (rectBounds)
        rectBounds.forEach { canvas.drawRect(it, paint) }
    }
}