package br.com.simplepass.loading_button_lib.customViews

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import br.com.simplepass.loading_button_lib.R
import br.com.simplepass.loading_button_lib.animatedDrawables.CircularProgressAnimatedDrawable
import br.com.simplepass.loading_button_lib.animatedDrawables.CircularRevealAnimatedDrawable
import br.com.simplepass.loading_button_lib.disposeAnimator
import br.com.simplepass.loading_button_lib.presentation.*

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

    private var paddingProgress = 0F //Todo: should be in View

    private var spinningBarWidth = 10F
    private var spinningBarColor = ContextCompat.getColor(context, android.R.color.black)

    private var finalCorner = 0F
    private var initialCorner = 0F

    internal var doneFillColor: Int = ContextCompat.getColor(context, android.R.color.black)
    internal lateinit var doneImage: Bitmap

    private val finalWidth: Int by lazy { finalHeight }
    var initialWidth = 0

    private val finalHeight: Int by lazy { height }
    private val initialHeight: Int by lazy { height }

    lateinit var drawable: GradientDrawable
    val initialText: CharSequence = text

    internal val morphAnimator by lazy {
        AnimatorSet().apply {
            playTogether(
                cornerAnimator(drawable, initialCorner, finalCorner),
                widthAnimator(this@CircularProgressButton, initialWidth, finalWidth),
                heightAnimator(this@CircularProgressButton, initialHeight, finalHeight)
            )

            addListener(morphListener(presenter::morphStart, presenter::morphEnd))
        }
    }

    private val morphRevertAnimator by lazy {
        AnimatorSet().apply {
            playTogether(
                cornerAnimator(drawable, finalCorner, initialCorner),
                widthAnimator(this@CircularProgressButton, finalWidth, initialWidth),
                heightAnimator(this@CircularProgressButton, finalHeight, initialHeight)
            )

            addListener(morphListener(presenter::morphRevertStart, presenter::morphRevertEnd))
        }
    }

    private val presenter = ProgressButtonPresenter(this)

    private val progressAnimatedDrawable: CircularProgressAnimatedDrawable by lazy {
        CircularProgressAnimatedDrawable(this, spinningBarWidth, spinningBarColor).apply {
            val offset = (width - height) / 2

            val left = offset + paddingProgress.toInt()
            val right = width - offset - paddingProgress.toInt()
            val bottom = height - paddingProgress.toInt()
            val top = paddingProgress.toInt()

            setBounds(left, top, right, bottom)
            callback = this@CircularProgressButton
        }
    }

    private val revealAnimatedDrawable: CircularRevealAnimatedDrawable by lazy {
        CircularRevealAnimatedDrawable(this, doneFillColor, doneImage).apply {
            setBounds(0, 0, width, height)
            callback = this@CircularProgressButton
        }
    }

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

        drawable = parseGradientDrawable(
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

    internal fun drawProgress(canvas: Canvas) {
        progressAnimatedDrawable.run {
            if (isRunning) {
                draw(canvas)
            } else {
                start()
            }
        }
    }

    internal fun drawDoneAnimation(canvas: Canvas) {
        revealAnimatedDrawable.draw(canvas)
    }

    internal fun startRevealAnimation() {
        revealAnimatedDrawable.start()
    }

    internal fun startMorphAnimation() {
        morphAnimator.start()
    }

    internal fun stopProgressAnimation() {
        progressAnimatedDrawable.stop()
    }

    internal fun stopMorphAnimation() {
        morphAnimator.end()
    }

    fun startAnimation() {
        presenter.startAnimation()
    }

    fun revertAnimation(onAnimationEndListener: () -> Unit = {}) {
        presenter.revertAnimation()

        morphRevertAnimator.apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEndListener()
                }
            })
        }.start()
    }

    fun stopAnimation() {
        presenter.stopAnimation()
    }

    fun doneLoadingAnimation(fillColor: Int, bitmap: Bitmap) {
        presenter.doneLoadingAnimation(fillColor, bitmap)
    }

    fun dispose() {
        morphAnimator.disposeAnimator()
        morphRevertAnimator.disposeAnimator()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        presenter.onDraw(canvas)
    }
}
