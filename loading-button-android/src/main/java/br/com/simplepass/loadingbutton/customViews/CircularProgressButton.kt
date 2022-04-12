package br.com.simplepass.loadingbutton.customViews

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import br.com.simplepass.loadingbutton.animatedDrawables.CircularProgressAnimatedDrawable
import br.com.simplepass.loadingbutton.animatedDrawables.CircularRevealAnimatedDrawable
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType
import br.com.simplepass.loadingbutton.disposeAnimator
import br.com.simplepass.loadingbutton.presentation.ProgressButtonPresenter
import br.com.simplepass.loadingbutton.presentation.State

open class CircularProgressButton : AppCompatButton, ProgressButton {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    override var paddingProgress = 0F

    override var spinningBarWidth = 10F
    override var spinningBarColor = ContextCompat.getColor(context, android.R.color.black)

    override var finalCorner = 0F
    override var initialCorner = 0F

    private lateinit var initialState: InitialState

    override val finalWidth: Int by lazy {
        val padding = Rect()
        drawableBackground.getPadding(padding)
        finalHeight - (Math.abs(padding.top - padding.left) * 2)
    }

    override val finalHeight: Int by lazy { height }
    private val initialHeight: Int by lazy { height }

    override var progressType: ProgressType
        get() = progressAnimatedDrawable.progressType
        set(value) {
            progressAnimatedDrawable.progressType = value
        }

    override lateinit var drawableBackground: Drawable

    private var savedAnimationEndListener: () -> Unit = {}

    private val presenter = ProgressButtonPresenter(this)

    private val morphAnimator by lazy {
        AnimatorSet().apply {
            playTogether(
                cornerAnimator(drawableBackground, initialCorner, finalCorner),
                widthAnimator(this@CircularProgressButton, initialState.initialWidth, finalWidth),
                heightAnimator(this@CircularProgressButton, initialHeight, finalHeight)
            )

            addListener(morphListener(presenter::morphStart, presenter::morphEnd))
        }
    }

    private val morphRevertAnimator by lazy {
        AnimatorSet().apply {
            playTogether(
                cornerAnimator(drawableBackground, finalCorner, initialCorner),
                widthAnimator(this@CircularProgressButton, finalWidth, initialState.initialWidth),
                heightAnimator(this@CircularProgressButton, finalHeight, initialHeight)
            )

            addListener(morphListener(presenter::morphRevertStart, presenter::morphRevertEnd))
        }
    }

    private val progressAnimatedDrawable: CircularProgressAnimatedDrawable by lazy {
        createProgressDrawable()
    }

    private lateinit var revealAnimatedDrawable: CircularRevealAnimatedDrawable

    override fun getState(): State = presenter.state

    override fun saveInitialState() {
        initialState = InitialState(width, text, compoundDrawables)
    }

    override fun recoverInitialState() {
        text = initialState.initialText
        setCompoundDrawables(
            initialState.compoundDrawables[0],
            initialState.compoundDrawables[1],
            initialState.compoundDrawables[2],
            initialState.compoundDrawables[3]
        )
    }

    override fun hideInitialState() {
        text = null
    }

    override fun drawProgress(canvas: Canvas) {
        progressAnimatedDrawable.drawProgress(canvas)
    }

    override fun drawDoneAnimation(canvas: Canvas) {
        revealAnimatedDrawable.draw(canvas)
    }

    override fun startRevealAnimation() {
        revealAnimatedDrawable.start()
    }

    override fun startMorphAnimation() {
        applyAnimationEndListener(morphAnimator, savedAnimationEndListener)
        morphAnimator.start()
    }

    override fun startMorphRevertAnimation() {
        applyAnimationEndListener(morphRevertAnimator, savedAnimationEndListener)
        morphRevertAnimator.start()
    }

    override fun stopProgressAnimation() {
        progressAnimatedDrawable.stop()
    }

    override fun stopMorphAnimation() {
        morphAnimator.end()
    }

    override fun startAnimation(onAnimationEndListener: () -> Unit) {
        savedAnimationEndListener = onAnimationEndListener
        presenter.startAnimation()
    }

    override fun revertAnimation(onAnimationEndListener: () -> Unit) {
        savedAnimationEndListener = onAnimationEndListener
        presenter.revertAnimation()
    }

    override fun stopAnimation() {
        presenter.stopAnimation()
    }

    override fun doneLoadingAnimation(fillColor: Int, bitmap: Bitmap) {
        presenter.doneLoadingAnimation(fillColor, bitmap)
    }

    override fun initRevealAnimation(fillColor: Int, bitmap: Bitmap) {
        revealAnimatedDrawable = createRevealAnimatedDrawable(fillColor, bitmap)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun dispose() {
        if (presenter.state != State.BEFORE_DRAW) {
            morphAnimator.disposeAnimator()
            morphRevertAnimator.disposeAnimator()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        presenter.onDraw(canvas)
    }

    override fun setProgress(value: Float) {
        if (presenter.validateSetProgress()) {
            progressAnimatedDrawable.progress = value
        } else {
            throw IllegalStateException("Set progress in being called in the wrong state: ${presenter.state}." +
                " Allowed states: ${State.PROGRESS}, ${State.MORPHING}, ${State.WAITING_PROGRESS}")
        }
    }

    data class InitialState(
        var initialWidth: Int,
        val initialText: CharSequence,
        val compoundDrawables: Array<Drawable>
    )
}
