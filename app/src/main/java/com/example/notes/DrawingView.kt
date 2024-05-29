package com.example.notes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var drawPath: Path? = null
    private var canvasPaint: Paint? = null
    private var drawPaint: Paint? = null
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        drawPath = Path()
        drawPaint = Paint()
        drawPaint!!.color = Color.BLACK
        drawPaint!!.isAntiAlias = true
        drawPaint!!.strokeWidth = 5f
        drawPaint!!.style = Paint.Style.STROKE
        drawPaint!!.strokeJoin = Paint.Join.ROUND
        drawPaint!!.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)
        canvas.drawPath(drawPath!!, drawPaint!!)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> drawPath!!.moveTo(touchX, touchY)
            MotionEvent.ACTION_MOVE -> drawPath!!.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> {
                drawCanvas!!.drawPath(drawPath!!, drawPaint!!)
                drawPath!!.reset()
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun getBitmap(): Bitmap {
        return canvasBitmap!!
    }

    fun setBitmap(bitmap: Bitmap) {
        canvasBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        drawCanvas = Canvas(canvasBitmap!!)
        invalidate()
    }
}
