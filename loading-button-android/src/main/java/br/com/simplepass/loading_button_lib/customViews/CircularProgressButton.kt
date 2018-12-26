package br.com.simplepass.loading_button_lib.customViews

import android.animation.*
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.*
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import br.com.simplepass.loading_button_lib.R
import br.com.simplepass.loading_button_lib.animatedDrawables.CircularProgressAnimatedDrawable
import br.com.simplepass.loading_button_lib.animatedDrawables.CircularRevealAnimatedDrawable
import br.com.simplepass.loading_button_lib.disposeAnimator

private enum class State {
    IDLE, MORPHING, MORPHING_REVERT, PROGRESS, DONE, STOPPED
}

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

        typedArray?.run {
            initialCorner = this.getDimension(R.styleable.CircularProgressButton_initialCornerAngle, 0f)
            finalCorner = this.getDimension(R.styleable.CircularProgressButton_finalCornerAngle, 100f)

            spinningBarWidth = this.getDimension(R.styleable.CircularProgressButton_spinning_bar_width, 10f)
            spinningBarColor = this.getColor(R.styleable.CircularProgressButton_spinning_bar_color, spinningBarColor)

            paddingProgress = this.getDimension(R.styleable.CircularProgressButton_spinning_bar_padding, 0F)
        }

        typedArray?.recycle()
        typedArrayBg?.recycle()
    }

    private var state: State = State.IDLE

    private var finalCorner = 0F
    private var initialCorner = 0F
    private lateinit var drawable: GradientDrawable

    private val finalWidth: Int by lazy { finalHeight }
    private var initialWidth = 0

    private val finalHeight: Int by lazy { this.height }
    private val initialHeight: Int by lazy { this.height }

    private var spinningBarWidth = 10F
    private var spinningBarColor = ContextCompat.getColor(context, android.R.color.black)

    private var paddingProgress = 0F

    private var waitingToStartDone: Boolean = false

    private var viewReady = false
    private var waitingToStartProgress = false

    private var doneFillColor: Int = ContextCompat.getColor(context, android.R.color.black)
    private lateinit var doneImage: Bitmap

    private val initialText = text

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

    private val morphAnimator by lazy {
        AnimatorSet().apply {
            playTogether(
                cornerAnimator(drawable, initialCorner, finalCorner),
                widthAnimator(this@CircularProgressButton, initialWidth, finalWidth),
                heightAnimator(this@CircularProgressButton, initialHeight, finalHeight)
            )

            addListener(morphListener(::morphStart, ::morphEnd))
        }
    }

    private val morphRevertAnimator by lazy {
        AnimatorSet().apply {
            playTogether(
                cornerAnimator(drawable, finalCorner, initialCorner),
                widthAnimator(this@CircularProgressButton, finalWidth, initialWidth),
                heightAnimator(this@CircularProgressButton, finalHeight, initialHeight)
            )

            addListener(morphListener(::morphRevertStart, ::morphRevertEnd))
        }
    }

    private fun morphStart() {
        isClickable = false
        waitingToStartProgress = false
        text = null
        state = State.MORPHING
        setCompoundDrawables(null, null, null, null)
    }

    private fun auxiliaryConfig() {
        initialWidth = width
    }

    private fun morphEnd() {
        isClickable = true

        if (waitingToStartDone) {
            waitingToStartDone = false

            val runnable = Runnable { revealAnimatedDrawable.start() }
            Handler().postDelayed(runnable, 50)
        }

        state = State.PROGRESS
    }

    private fun morphRevertStart() {
        isClickable = false
        state = State.MORPHING
    }

    private fun morphRevertEnd() {
        isClickable = true
        state = State.IDLE
        text = initialText

        //        setCompoundDrawablesRelative(mParams.mDrawables[0], mParams.mDrawables[1], mParams.mDrawables[2], mParams.mDrawables[3])
    }

    fun startAnimation() {
        if (state != State.IDLE) {
            return
        }

        if (!viewReady) {
            waitingToStartProgress = true
            return
        }

        morphAnimator.start()
    }

    fun revertAnimation(onAnimationEndListener: () -> Unit = {}) {
        if (state == State.IDLE) {
            return
        }

        when (state) {
            State.IDLE, State.MORPHING_REVERT -> return
            State.MORPHING                    -> {
                morphAnimator.end()
            }
            State.PROGRESS                    -> {
                progressAnimatedDrawable.stop()
            }
            else                              -> {
            }
        }

        morphRevertAnimator.apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEndListener()
                }
            })
        }.start()
    }


    fun stopAnimation() {
        state = State.STOPPED

        if (state == State.PROGRESS) {
            progressAnimatedDrawable.stop()
        }
    }

    fun doneLoadingAnimation(fillColor: Int, bitmap: Bitmap) {
        doneFillColor = fillColor
        doneImage = bitmap

        when (state) {
            State.PROGRESS -> {
                progressAnimatedDrawable.stop()
                revealAnimatedDrawable.start()
            }
            State.MORPHING -> {
                waitingToStartDone = true
            }
            else -> {}
        }

        state = State.DONE
    }

    fun dispose() {
        morphAnimator.disposeAnimator()
        morphRevertAnimator.disposeAnimator()
    }

    private fun drawProgress(canvas: Canvas) {
        progressAnimatedDrawable.run {
            if (isRunning) {
                draw(canvas)
            } else {
                start()
            }
        }
    }

    private fun drawDoneAnimation(canvas: Canvas) {
        revealAnimatedDrawable.draw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!viewReady) {
            auxiliaryConfig()
        }

        viewReady = true

        when (state) {
            State.IDLE     -> {
                if (waitingToStartProgress) {
                    morphAnimator.start()
                }
            }
            State.PROGRESS -> drawProgress(canvas)
            State.DONE     -> drawDoneAnimation(canvas)
            else           -> {
            }
        }
    }
}

private fun parseGradientDrawable(drawable: Drawable): GradientDrawable =
    when (drawable) {
        is GradientDrawable  -> drawable
        is ColorDrawable     -> GradientDrawable().apply { setColor(drawable.color) }
        is InsetDrawable     -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                drawable.drawable?.let { innerDrawable ->
                    parseGradientDrawable(innerDrawable)
                }
                    ?: throw RuntimeException("Error reading background... Use a shape or a color in xml!")
            } else {
                throw RuntimeException("Error reading background... Use a shape or a color in xml!")
            }
        }
        is StateListDrawable -> {
            if (drawable.current is GradientDrawable) {
                drawable.current as GradientDrawable
            } else {
                throw RuntimeException("Error reading background... Use a shape or a color in xml!")
            }
        }
        else                 -> throw RuntimeException("Error reading background... Use a shape or a color in xml!")

    }


private fun cornerAnimator(drawable: GradientDrawable, initial: Float, final: Float) =
    ObjectAnimator.ofFloat(drawable, "cornerRadius", initial, final)

private fun widthAnimator(view: View, initial: Int, final: Int) =
    ValueAnimator.ofInt(initial, final).apply {
        addUpdateListener { animation ->
            val layoutParams = view.layoutParams
            layoutParams.width = animation.animatedValue as Int
            view.layoutParams = layoutParams
        }
    }

private fun heightAnimator(view: View, initial: Int, final: Int) =
    ValueAnimator.ofInt(initial, final).apply {
        addUpdateListener { animation ->
            val layoutParams = view.layoutParams
            layoutParams.height = animation.animatedValue as Int
            view.layoutParams = layoutParams
        }
    }

private fun morphListener(morphStartFn: () -> Unit, morphEndFn: () -> Unit) =
    object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            morphEndFn()
        }

        override fun onAnimationStart(animation: Animator?) {
            morphStartFn()
        }
    }
