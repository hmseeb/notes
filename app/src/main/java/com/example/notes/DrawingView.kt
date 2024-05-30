package com.example.notes

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var path = Path()
    private var paint = Paint()
    private var canvasBitmap: Bitmap? = null
    private var drawCanvas: Canvas? = null
    private var canvasPaint: Paint = Paint(Paint.DITHER_FLAG)
    private var isDrawingEnabled = false

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (canvasBitmap == null) {
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            drawCanvas = Canvas(canvasBitmap!!)
        } else {
            drawCanvas = Canvas(canvasBitmap!!)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (canvasBitmap != null) {
            canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)
            canvas.drawPath(path, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isDrawingEnabled) return false

        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(touchX, touchY)
            MotionEvent.ACTION_MOVE -> path.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> drawCanvas?.drawPath(path, paint)
        }
        invalidate()
        return true
    }

    fun clearDrawing() {
        path.reset()
        invalidate()
    }

    fun getDrawing(): Bitmap? {
        return canvasBitmap
    }

    fun setDrawingEnabled(enabled: Boolean) {
        isDrawingEnabled = enabled
    }

    fun loadDrawing(bitmap: Bitmap) {
        canvasBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        drawCanvas = Canvas(canvasBitmap!!)
        invalidate()
    }
}