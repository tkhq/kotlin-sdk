package com.example.kotlin_demo_wallet.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kotlin_demo_wallet.R
import com.google.android.material.divider.MaterialDivider
import android.widget.TextView

class OrSeparatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val dividerStart: MaterialDivider
    private val dividerEnd: MaterialDivider
    private val label: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.or_divider, this, true)
        dividerStart = findViewById(R.id.dividerStart)
        dividerEnd = findViewById(R.id.dividerEnd)
        label = findViewById(R.id.orText)

        val a = context.obtainStyledAttributes(attrs, R.styleable.OrSeparatorView, defStyle, 0)

        val labelText = a.getString(R.styleable.OrSeparatorView_labelText) ?: "or"
        val dividerColor = a.getColor(
            R.styleable.OrSeparatorView_dividerColor,
            dividerStart.dividerColor // defaults to current
        )
        val startMargin = a.getDimensionPixelSize(R.styleable.OrSeparatorView_startMargin, dp(20))
        val endMargin = a.getDimensionPixelSize(R.styleable.OrSeparatorView_endMargin, dp(20))
        val labelTopMargin = a.getDimensionPixelSize(R.styleable.OrSeparatorView_labelTopMargin, dp(20))
        val thickness = a.getDimensionPixelSize(R.styleable.OrSeparatorView_dividerThickness, dp(1))
        a.recycle()

        setLabelText(labelText)
        setDividerColor(dividerColor)
        setDividerThickness(thickness)
        setSideMargins(startMargin, endMargin)
        setLabelTopMargin(labelTopMargin)
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    fun setLabelText(text: CharSequence) { label.text = text }

    fun setDividerColor(color: Int) {
        dividerStart.dividerColor = color
        dividerEnd.dividerColor = color
    }

    fun setDividerThickness(px: Int) {
        (dividerStart.layoutParams).height = px
        (dividerEnd.layoutParams).height = px
        dividerStart.requestLayout()
        dividerEnd.requestLayout()
    }

    fun setSideMargins(startPx: Int, endPx: Int) {
        (dividerStart.layoutParams as MarginLayoutParams).marginStart = startPx
        (dividerEnd.layoutParams as MarginLayoutParams).marginEnd = endPx
        dividerStart.requestLayout()
        dividerEnd.requestLayout()
    }

    fun setLabelTopMargin(topPx: Int) {
        val lp = label.layoutParams as MarginLayoutParams
        lp.topMargin = topPx
        label.requestLayout()
    }
}