package com.example.sliderpointview

/**
 * Created by anweshmishra on 11/05/18.
 */

import android.app.Activity
import android.content.Context
import android.view.View
import android.graphics.*
import android.view.MotionEvent

class SliderPointView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State (var dir : Float = 0f, var scale : Float = 0f, var prevScale : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(scale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SliderPoint(var i : Int, val state : State = State()) {

        fun draw(canvas : Canvas, paint : Paint) {
            paint.color = Color.parseColor("#2980b9")
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val hSize : Float = h / 10
            val r : Float = Math.min(w, h) / 20
            val x : Float = 0.05f * w + 0.9f * w * state.scale
            canvas.save()
            canvas.translate(0f, h / 2)
            canvas.save()
            canvas.translate(x, 0f)
            val path : Path = Path()
            path.moveTo(0f, -hSize / 2)
            path.lineTo(-r, -1.5f * hSize)
            path.arcTo(RectF(-r, -1.5f * hSize -r , r, -1.5f * hSize + r), 180f, 180f)
            path.lineTo(0f, -hSize / 2)
            canvas.drawPath(path, paint)
            canvas.restore()
            canvas.drawRect(RectF(0.05f * w, -hSize/2, x, hSize/2), paint)
            canvas.drawCircle(x, 0f, hSize/2, paint)
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

    }

    data class Renderer(var view : SliderPointView) {

        private val animator : Animator = Animator(view)

        private val sliderPoint : SliderPoint = SliderPoint(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            sliderPoint.draw(canvas, paint)
            animator.animate {
                sliderPoint.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            sliderPoint.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : SliderPointView {
            val view : SliderPointView = SliderPointView(activity)
            activity.setContentView(view)
            return view
        }
    }

}


