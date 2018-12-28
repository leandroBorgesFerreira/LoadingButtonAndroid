package br.com.simplepass.loading_button_lib.customViews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import br.com.simplepass.loading_button_lib.R
import br.com.simplepass.loading_button_lib.presentation.ProgressButtonPresenter
import br.com.simplepass.loading_button_lib.presentation.parseGradientDrawable

class CircularProgressButton : AppCompatButton {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    private val presenter = ProgressButtonPresenter(this)

    private fun init(attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        val typedArray: TypedArray? = attrs?.run {
            context.obtainStyledAttributes(
                this,
                R.styleable.CircularProgressButton,
                defStyleAttr,
                0
            )
        }

        val typedArrayBg: TypedArray? = attrs?.run {
            val attrsArray = intArrayOf(android.R.attr.background)
            context.obtainStyledAttributes(this, attrsArray, defStyleAttr, 0)
        }

        presenter.drawable = parseGradientDrawable(
            typedArrayBg?.getDrawable(0)
                ?: ContextCompat.getDrawable(context, R.drawable.shape_default)!!
        )

        typedArray?.let { tArray ->
            presenter.run {
                initialCorner = tArray.getDimension(R.styleable.CircularProgressButton_initialCornerAngle, 0f)
                finalCorner = tArray.getDimension(R.styleable.CircularProgressButton_finalCornerAngle, 100f)

                spinningBarWidth = tArray.getDimension(R.styleable.CircularProgressButton_spinning_bar_width, 10f)
                spinningBarColor = tArray.getColor(R.styleable.CircularProgressButton_spinning_bar_color, spinningBarColor)

                paddingProgress = tArray.getDimension(R.styleable.CircularProgressButton_spinning_bar_padding, 0F)
            }
        }

        typedArray?.recycle()
        typedArrayBg?.recycle()
    }

    fun startAnimation() {
        presenter.startAnimation()
    }

    fun revertAnimation(onAnimationEndListener: () -> Unit = {}) {
        presenter.revertAnimation(onAnimationEndListener)
    }

    fun stopAnimation() {
        presenter.stopAnimation()
    }

    fun doneLoadingAnimation(fillColor: Int, bitmap: Bitmap) {
        presenter.doneLoadingAnimation(fillColor, bitmap)
    }

    fun dispose() {
        presenter.dispose()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        presenter.onDraw(canvas)
    }
}
