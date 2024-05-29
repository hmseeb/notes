package com.example.notes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

enum class ButtonsState {
    GONE, LEFT_VISIBLE
}

interface SwipeControllerActions {
    fun onLeftClicked(position: Int)
    fun onCenterClicked(position: Int)
    fun onRightClicked(position: Int)
}

class SwipeController(private val actions: SwipeControllerActions) : ItemTouchHelper.Callback() {

    private var swipeBack = false
    private var buttonShowedState: ButtonsState = ButtonsState.GONE
    private var buttonInstance: RectF? = null
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null

    private val buttonWidth = 200f

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Do nothing
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                val maxDx = -buttonWidth * 3
                if (dX < maxDx) {
                    super.onChildDraw(c, recyclerView, viewHolder, maxDx, dY, actionState, isCurrentlyActive)
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            } else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        currentItemViewHolder = viewHolder
        drawButtons(c, viewHolder)
    }

    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (dX < -buttonWidth) buttonShowedState = ButtonsState.LEFT_VISIBLE

                if (buttonShowedState != ButtonsState.GONE) {
                    setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            false
        }
    }

    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super@SwipeController.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    0f,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                recyclerView.setOnTouchListener { _, _ -> false }
                setItemsClickable(recyclerView, true)
                swipeBack = false
                if (buttonInstance != null && buttonInstance!!.contains(event.x, event.y)) {
                    when {
                        buttonShowedState == ButtonsState.LEFT_VISIBLE && event.x < buttonInstance!!.centerX() -> {
                            actions.onLeftClicked(viewHolder.adapterPosition)
                        }
                        buttonShowedState == ButtonsState.LEFT_VISIBLE && event.x > buttonInstance!!.centerX() -> {
                            actions.onRightClicked(viewHolder.adapterPosition)
                        }
                    }
                }
                buttonShowedState = ButtonsState.GONE
                currentItemViewHolder = null
            }
            false
        }
    }

    private fun setItemsClickable(recyclerView: RecyclerView, clickable: Boolean) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = clickable
        }
    }

    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val buttonWidthWithoutPadding = buttonWidth - 20
        val corners = 16f

        val itemView = viewHolder.itemView
        val p = Paint()

        val leftButton = RectF(
            itemView.right - buttonWidthWithoutPadding * 3,
            itemView.top.toFloat(),
            itemView.right - buttonWidthWithoutPadding * 2,
            itemView.bottom.toFloat()
        )
        p.color = Color.BLUE
        c.drawRoundRect(leftButton, corners, corners, p)
        drawText("Edit", c, leftButton, p)

        val centerButton = RectF(
            itemView.right - buttonWidthWithoutPadding * 2,
            itemView.top.toFloat(),
            itemView.right - buttonWidthWithoutPadding,
            itemView.bottom.toFloat()
        )
        p.color = Color.YELLOW
        c.drawRoundRect(centerButton, corners, corners, p)
        drawText("Fav", c, centerButton, p)

        val rightButton = RectF(
            itemView.right - buttonWidthWithoutPadding,
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat()
        )
        p.color = Color.RED
        c.drawRoundRect(rightButton, corners, corners, p)
        drawText("Delete", c, rightButton, p)

        buttonInstance = null
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton
        }
    }

    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
        val textSize = 40f
        p.color = Color.WHITE
        p.isAntiAlias = true
        p.textSize = textSize

        val textWidth = p.measureText(text)
        c.drawText(
            text,
            button.centerX() - textWidth / 2,
            button.centerY() + textSize / 2,
            p
        )
    }
}
