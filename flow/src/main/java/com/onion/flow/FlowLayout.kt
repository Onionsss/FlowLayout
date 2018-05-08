package com.onion.flow

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by zhangqi on 2018/5/4.
 */
class FlowLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(context, attrs, defStyleAttr) {

    constructor(context: Context?,attrs: AttributeSet?): this(context,attrs,0)

    constructor(context: Context?): this(context,null)

    private var TAG = "FlowLayout"

    private var mTitles: ArrayList<String> = arrayListOf()

    var flowListener: FlowClickListener? = null
    private var mWidth = 0
    private var mHeight = 0

    private var mPaddingLeft = 0
    private var mPaddingRight = 0
    private var mPaddingTop = 0
    private var mPaddingBottom = 0
    private var mMarginLeft = 0
    private var mMarginRight = 0
    private var mMarginTop = 0
    private var mMarginBottom = 0
    private var mTextSize = 0
    private var mTextColor = Color.BLACK
    private var mBackgroundRes = R.drawable.select

    init {
        var a: TypedArray = context?.obtainStyledAttributes(attrs,R.styleable.FlowLayout)!!

        mPaddingLeft = a.getDimension(R.styleable.FlowLayout_flow_paddingLeft,dp2px(10f).toFloat()).toInt()
        mPaddingRight = a.getDimension(R.styleable.FlowLayout_flow_paddingRight,dp2px(10f).toFloat()).toInt()
        mPaddingTop = a.getDimension(R.styleable.FlowLayout_flow_paddingTop,dp2px(5f).toFloat()).toInt()
        mPaddingBottom = a.getDimension(R.styleable.FlowLayout_flow_paddingBottom,dp2px(5f).toFloat()).toInt()
        mMarginLeft = a.getDimension(R.styleable.FlowLayout_flow_marginLeft,dp2px(8f).toFloat()).toInt()
        mMarginRight = a.getDimension(R.styleable.FlowLayout_flow_marginRight,dp2px(8f).toFloat()).toInt()
        mMarginTop = a.getDimension(R.styleable.FlowLayout_flow_marginTop,dp2px(5f).toFloat()).toInt()
        mMarginBottom = a.getDimension(R.styleable.FlowLayout_flow_marginBottom,dp2px(5f).toFloat()).toInt()
        mBackgroundRes = a.getResourceId(R.styleable.FlowLayout_flow_backgroundRes,R.drawable.select)
        mTextSize = a.getDimension(R.styleable.FlowLayout_flow_textSize,sp2px(14f).toFloat()).toInt()
        mTextColor = a.getColor(R.styleable.FlowLayout_flow_textColor,Color.BLACK)
        a.recycle()
    }

    fun setData(titles: List<String>): Int{
        mTitles.clear()
        mTitles.addAll(titles)
        return mTitles.size
    }

    fun start(){
        mTitles.forEachIndexed { index,value ->
            var tv = TextView(context)
            tv.text = value
            tv.gravity = Gravity.CENTER
            var mar = MarginLayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
            mar.leftMargin = mMarginLeft
            mar.rightMargin = mMarginRight
            mar.topMargin = mMarginTop
            mar.bottomMargin = mMarginBottom
            tv.layoutParams = mar
            tv.setPadding(mPaddingLeft,mPaddingTop,mPaddingRight,mPaddingBottom)
            tv.setBackgroundResource(mBackgroundRes)
            tv.setTextColor(mTextColor)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize.toFloat())
            tv.isClickable = true

            tv.setOnClickListener {
                flowListener?.let {
                    flowListener?.onClick(value,index)
                }
            }
            addView(tv)
        }
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var width = 0
        var height = 0

        var lineHeight = 0
        var lineWidth = 0

        for(i in 0 until childCount){
            var child = getChildAt(i)
            measureChild(child,widthMeasureSpec,heightMeasureSpec)
            val layoutParams: MarginLayoutParams = child.layoutParams as MarginLayoutParams
            var childWidth = child.measuredWidth+layoutParams.leftMargin+layoutParams.rightMargin
            var childHeight = child.measuredHeight+layoutParams.topMargin+layoutParams.bottomMargin
            if(lineWidth + childWidth > widthSize - paddingLeft - paddingRight){
                var w = Math.max(lineWidth,childWidth)
                width = Math.max(width,w)
                lineWidth = childWidth
                height += lineHeight
                lineHeight = childHeight
            }else{
                lineWidth += childWidth
                lineHeight = Math.max(lineHeight,childHeight)
            }
            if(i == childCount - 1){
                width = Math.max(lineWidth,width)
                height += lineHeight
            }
        }

        setMeasuredDimension(if(widthMode == MeasureSpec.EXACTLY) widthSize else width,
                if(heightMode == MeasureSpec.EXACTLY) heightSize else height + paddingTop + paddingBottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mWidth = w
        mHeight = h
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        var lineHeight = paddingTop
        var lineWidth = 0

        for (i in 0 until childCount){
            var child = getChildAt(i)
            val layoutParams = child.layoutParams as MarginLayoutParams

            if(lineWidth + child.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin + paddingLeft
                    > mWidth - paddingRight){
                lineWidth = 0
                lineHeight += child.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin
            }

            var l = lineWidth + layoutParams.leftMargin + paddingLeft
            var t = lineHeight + layoutParams.topMargin
            var r = l + child.measuredWidth
            var b = t + child.measuredHeight

            lineWidth = r + layoutParams.rightMargin - paddingLeft

            /**
             * 摆放子View
             */
            child.layout(l,t,r,b)
        }
    }

    private fun dp2px(dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private fun sp2px(sp: Float): Int {
        val scale = context.resources.displayMetrics.scaledDensity
        return (sp * scale + 0.5f).toInt()
    }
}