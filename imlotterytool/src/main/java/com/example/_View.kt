

@file:Suppress("unused", "NOTHING_TO_INLINE")
package com.example

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.setPadding


inline fun View.paddingAll(@Px size: Int) {
    this.setPadding(size)
}

inline fun View.paddingOnly(@Px left: Int = 0, @Px top: Int = 0, @Px right: Int = 0, @Px bottom: Int = 0) {
    this.setPadding(left, top, right, bottom)
}

inline fun View.paddingSymmetric(@Px vertical: Int = 0, @Px horizontal: Int = 0) {
    this.setPadding(horizontal, vertical, horizontal, vertical)
}

fun createBitmapSafely(width: Int, height: Int, config: Bitmap.Config, retryCount: Int = 3): Bitmap? {
    try {
        return Bitmap.createBitmap(width, height, config)
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        if (retryCount > 0) {
            System.gc()
            return createBitmapSafely(width, height, config, retryCount - 1)
        }
        return null
    }
}

inline fun View.onGlobalLayout(crossinline callback: () -> Unit) {
    this.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            callback()
        }
    })
}

inline fun View.afterMeasured(crossinline callback: View.() -> Unit) {
    this.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                callback()
            }
        }
    })
}

/**
 * 处理第N次的点击事件
 *
 * @param count    要处理第几次的点击事件
 * @param interval [count]连续两次点击事件的最大时间间隔，如果超过这个值会重新统计次数
 */
inline fun View.onClickN(count: Int = 1, interval: Long = 1000, listener: View.OnClickListener) {
    this.onClick(object: View.OnClickListener {
        var clickCount = 0
        var lastClickTime = 0L

        override fun onClick(v: View) {
            val currentTime = System.currentTimeMillis()
            if (currentTime < lastClickTime || currentTime - lastClickTime > interval) {
                clickCount = 1
            } else {
                clickCount++
            }
            lastClickTime = currentTime

            if (clickCount == count) {
                clickCount = 0
                lastClickTime = 0L
                listener.onClick(v)
            }
        }
    })
}

inline fun View.onClickN(count: Int = 1, interval: Long = 1000, crossinline listener: (View) -> Unit) {
    this.onClickN(count, interval, View.OnClickListener { listener(it) })
}

inline fun <reified T: View> Activity.onClickN(@IdRes id: Int, count: Int = 1, interval: Long = 1000, listener: View.OnClickListener): T? {
    return this.findViewById<T>(id)?.apply { this.onClickN(count, interval, listener) }
}

inline fun <reified T: View> Activity.onClickN(@IdRes id: Int, count: Int = 1, interval: Long = 1000, crossinline listener: (View) -> Unit): T? {
    return this.findViewById<T>(id)?.apply { this.onClickN(count, interval, listener) }
}

inline fun <reified T: View> View.onClickN(@IdRes id: Int, count: Int = 1, interval: Long = 1000, listener: View.OnClickListener): T? {
    return this.findViewById<T>(id)?.apply { this.onClickN(count, interval, listener) }
}

inline fun <reified T: View> View.onClickN(@IdRes id: Int, count: Int = 1, interval: Long = 1000, crossinline listener: (View) -> Unit): T? {
    return findViewById<T>(id)?.apply { this.onClickN(count, interval, listener) }
}

inline fun View.onClick(listener: View.OnClickListener) {
    this.setOnClickListener(listener)
}

inline fun View.onClick(crossinline action: (View) -> Unit) {
    this.setOnClickListener { action(it) }
}

inline fun View.onLongClick(listener: View.OnLongClickListener) {
    this.setOnLongClickListener(listener)
}

inline fun View.onLongClick(crossinline action: (View) -> Boolean) {
    this.setOnLongClickListener { action(it) }
}

inline fun View.onTouch(listener: View.OnTouchListener) {
    this.setOnTouchListener(listener)
}

@SuppressLint("ClickableViewAccessibility")
inline fun View.onTouch(crossinline action: (View, MotionEvent) -> Boolean) {
    this.setOnTouchListener { v, event -> action(v, event) }
}

inline fun <reified T: View> Activity.onClick(@IdRes id: Int, listener: View.OnClickListener): T? {
    return this.findViewById<T>(id)?.apply { this.onClick(listener) }
}

inline fun <reified T: View> Activity.onClick(@IdRes id: Int, crossinline action: (View) -> Unit): T? {
    return this.findViewById<T>(id)?.apply { this.onClick(action) }
}

inline fun <reified T: View> View.onClick(@IdRes id: Int, listener: View.OnClickListener): T? {
    return this.findViewById<T>(id)?.apply { this.onClick(listener) }
}

inline fun <reified T: View> View.onClick(@IdRes id: Int, crossinline action: (View) -> Unit): T? {
    return this.findViewById<T>(id)?.apply { this.onClick(action) }
}

inline fun <reified T: View> Activity.onLongClick(@IdRes id: Int, listener: View.OnLongClickListener): T? {
    return this.findViewById<T>(id)?.apply { this.onLongClick(listener) }
}

inline fun <reified T: View> Activity.onLongClick(@IdRes id: Int, crossinline action: (View) -> Boolean): T? {
    return this.findViewById<T>(id)?.apply { this.onLongClick(action) }
}

inline fun <reified T: View> View.onLongClick(@IdRes id: Int, listener: View.OnLongClickListener): T? {
    return this.findViewById<T>(id)?.apply { this.onLongClick(listener) }
}

inline fun <reified T: View> View.onLongClick(@IdRes id: Int, crossinline action: (View) -> Boolean): T? {
    return this.findViewById<T>(id)?.apply { this.onLongClick(action) }
}

inline fun <reified T: View> Activity.onTouch(@IdRes id: Int, listener: View.OnTouchListener): T? {
    return this.findViewById<T>(id)?.apply { this.onTouch(listener) }
}

inline fun <reified T: View> Activity.onTouch(@IdRes id: Int, crossinline action: (View, MotionEvent) -> Boolean): T? {
    return this.findViewById<T>(id)?.apply { this.onTouch(action) }
}

inline fun <reified T: View> View.onTouch(@IdRes id: Int, listener: View.OnTouchListener): T? {
    return this.findViewById<T>(id)?.apply { this.onTouch(listener) }
}

inline fun <reified T: View> View.onTouch(@IdRes id: Int, crossinline action: (View, MotionEvent) -> Boolean): T? {
    return this.findViewById<T>(id)?.apply { this.onTouch(action) }
}


inline fun Context.getColorResource(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

inline fun <reified T: View> T.setBackgroundColorResource(@ColorRes resId: Int): T {
    this.setBackgroundColor(this@setBackgroundColorResource.context.getColorResource(resId))
    return this
}

inline fun <reified T: View> T.setBackgroundColorResource(context: Context, @ColorRes resId: Int): T {
    this.setBackgroundColor(context.getColorResource(resId))
    return this
}


inline fun <reified T: View> Activity.setBackgroundResource(@IdRes id: Int, @DrawableRes resId: Int): T? {
    return this.findViewById<T>(id)?.apply { this.setBackgroundResource(resId) }
}

inline fun <reified T: View> View.setBackgroundResource(@IdRes id: Int, @DrawableRes resId: Int): T? {
    return this.findViewById<T>(id)?.apply { this.setBackgroundResource(resId) }
}

inline fun <reified T: View> Activity.setBackgroundColor(@IdRes id: Int, @ColorInt resId: Int): T? {
    return this.findViewById<T>(id)?.apply { this.setBackgroundColor(resId) }
}

inline fun <reified T: View> View.setBackgroundColor(@IdRes id: Int, @ColorInt resId: Int): T? {
    return this.findViewById<T>(id)?.apply { this.setBackgroundColor(resId) }
}

inline fun <reified T: View> Activity.setBackgroundColorResource(@IdRes id: Int, @ColorRes resId: Int): T? {
    return this.findViewById<T>(id)?.apply { this.setBackgroundColor(this@setBackgroundColorResource.getColorResource(resId)) }
}

inline fun <reified T: View> View.setBackgroundColorResource(@IdRes id: Int, @ColorRes resId: Int): T? {
    return this.findViewById<T>(id)?.apply { this.setBackgroundColor(this@setBackgroundColorResource.context.getColorResource(resId)) }
}

inline fun <reified T: View> Activity.setBackground(@IdRes id: Int, drawable: Drawable?): T? {
    return this.findViewById<T>(id)?.apply { this.background = drawable }
}

inline fun <reified T: View> View.setBackground(@IdRes id: Int, drawable: Drawable?): T? {
    return this.findViewById<T>(id)?.apply { this.background = drawable }
}

inline fun <reified T: View> Activity.setVisibility(@IdRes id: Int, visibility: Int): T? {
    return this.findViewById<T>(id)?.apply { this.visibility = visibility }
}

inline fun <reified T: View> View.setVisibility(@IdRes id: Int, visibility: Int): T? {
    return this.findViewById<T>(id)?.apply { this.visibility = visibility}
}

inline fun <reified T: View> Activity.setVisibility(@IdRes id: Int, isVisibility: Boolean): T? {
    return this.findViewById<T>(id)?.apply {
        if (isVisibility) {
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }
}

inline fun <reified T: View> View.setVisibility(@IdRes id: Int, isVisibility: Boolean): T? {
    return this.findViewById<T>(id)?.apply {
        if (isVisibility) {
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }
}

inline fun <reified T: View> Activity.setInvisibility(@IdRes id: Int, isInvisible: Boolean): T? {
    return this.findViewById<T>(id)?.apply {
        if (isInvisible) {
            this.visibility = View.INVISIBLE
        } else {
            this.visibility = View.VISIBLE
        }
    }
}

inline fun <reified T: View> View.setInvisibility(@IdRes id: Int, isInvisible: Boolean): T? {
    return this.findViewById<T>(id)?.apply {
        if (isInvisible) {
            this.visibility = View.INVISIBLE
        } else {
            this.visibility = View.VISIBLE
        }
    }
}


inline fun <reified T: TextView> Activity.setText(@IdRes id: Int, @IdRes resId: Int): T? {
    return this.findViewById<T>(id)?.apply { this.setText(resId) }
}

inline fun <reified T: TextView> View.setText(@IdRes id: Int, @IdRes resId: Int): T? {
    return this.findViewById<T>(id)?.apply { this.setText(resId) }
}

inline fun <reified T: TextView> Activity.setText(@IdRes id: Int, text: String): T? {
    return this.findViewById<T>(id)?.apply { this.text = text }
}

inline fun <reified T: TextView> View.setText(@IdRes id: Int, text: String): T? {
    return this.findViewById<T>(id)?.apply { this.text = text }
}

inline fun <reified T: TextView> T.setTextColorResource(@ColorRes id: Int): T {
    this.setTextColor(this.context.getColorResource(id))
    return this
}

inline fun <reified T: TextView> T.setTextColorResource(context: Context, @ColorRes id: Int): T {
    this.setTextColor(context.getColorResource(id))
    return this
}

inline fun TextView.setCompoundDrawables(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    setCompoundDrawables(
        context.getBoundDrawable(left),
        context.getBoundDrawable(top),
        context.getBoundDrawable(right),
        context.getBoundDrawable(bottom)
    )
}

inline fun TextView.setCompoundDrawables(context: Context, left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    setCompoundDrawables(
        context.getBoundDrawable(left),
        context.getBoundDrawable(top),
        context.getBoundDrawable(right),
        context.getBoundDrawable(bottom)
    )
}

inline fun Context.getBoundDrawable(id: Int): Drawable? {
    if (id <= 0) {
        return null
    }

    val drawable = ContextCompat.getDrawable(this, id) ?: return null
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    return drawable
}


inline fun <reified T: ImageView> Activity.setImageResource(@IdRes id: Int, @DrawableRes resId: Int): T? {
    return this.findViewById<T>(id)?.apply { this.setImageResource(resId) }
}

inline fun <reified T: ImageView> View.setImageResource(@IdRes id: Int, @DrawableRes resId: Int): T? {
    return this.findViewById<T>(id)?.apply { this.setImageResource(resId) }
}

inline fun <reified T: ImageView> Activity.setImageBitmap(@IdRes id: Int, bm: Bitmap?): T? {
    return this.findViewById<T>(id)?.apply { this.setImageBitmap(bm) }
}

inline fun <reified T: ImageView> View.setImageBitmap(@IdRes id: Int, bm: Bitmap?): T? {
    return this.findViewById<T>(id)?.apply { this.setImageBitmap(bm) }
}

inline fun <reified T: ImageView> Activity.setImageDrawable(@IdRes id: Int, drawable: Drawable?): T? {
    return this.findViewById<T>(id)?.apply { this.setImageDrawable(drawable) }
}

inline fun <reified T: ImageView> View.setImageDrawable(@IdRes id: Int, drawable: Drawable?): T? {
    return this.findViewById<T>(id)?.apply { this.setImageDrawable(drawable) }
}

inline fun View.uiUpdate(crossinline action: () -> Unit) {
    if (!this.isAttachedToWindow) {
        return
    }

    if (Looper.myLooper() == Looper.getMainLooper()) {
        action()
    } else {
        post {
            if (this.isAttachedToWindow) {
                action()
            }
        }
    }
}

inline fun View.uiUpdate(action: Runnable) {
    if (!this.isAttachedToWindow) {
        return
    }

    if (Looper.myLooper() == Looper.getMainLooper()) {
        action.run()
    } else {
        post {
            if (this.isAttachedToWindow) {
                action.run()
            }
        }
    }
}

inline fun getRoundDrawable(radius: Int, color: Int, strokeWidth: Int = 0, strokeColor: Int = Color.TRANSPARENT): GradientDrawable {
    return getRoundDrawable(radius.toFloat(), color, strokeWidth, strokeColor)
}

inline fun getRoundDrawable(radius: Float, color: Int, strokeWidth: Int = 0, strokeColor: Int = Color.TRANSPARENT): GradientDrawable {
    val cornerRadii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
    return getRoundDrawable(cornerRadii, color, strokeWidth, strokeColor)
}

/**
 * 创建圆角Drawable
 *
 * @param cornerRadii 左上、右上、右下、左下的圆角半径，共八个值（每个角两个半径值）
 * @param color       Drawable颜色
 * @param strokeWidth 边框宽度，默认为0（没有边框）
 * @param strokeColor 边框颜色
 */
inline fun getRoundDrawable(cornerRadii: FloatArray, color: Int, strokeWidth: Int = 0, strokeColor: Int = Color.TRANSPARENT): GradientDrawable {
    return GradientDrawable().apply {
        this.cornerRadii = cornerRadii
        setColor(color)
        if (strokeWidth > 0) {
            setStroke(strokeWidth, strokeColor)
        }
    }
}

/**
 * 修改[Drawable]颜色
 */
inline fun Drawable.newTint(@ColorInt tintColor: Int): Drawable {
    val wrappedDrawable = DrawableCompat.wrap(this)
    wrappedDrawable.mutate()
    DrawableCompat.setTint(wrappedDrawable, tintColor)
    return wrappedDrawable
}

/**
 * 修改[Drawable]颜色
 */
inline fun Drawable.newTint(context: Context, @ColorRes tintColorId: Int): Drawable {
    val wrappedDrawable = DrawableCompat.wrap(this)
    wrappedDrawable.mutate()
    DrawableCompat.setTint(wrappedDrawable, context.getColorResource(tintColorId))
    return wrappedDrawable
}

inline fun Canvas.saveAndRestore(action: (() -> Unit)) {
    save()
    action()
    restore()
}

