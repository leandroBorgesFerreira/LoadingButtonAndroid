package br.com.simplepass.loadingbutton.presentation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import br.com.simplepass.loadingbutton.customViews.ProgressButton

internal enum class State {
    BEFORE_DRAW,
    IDLE,
    MORPHING,
    MORPHING_REVERT,
    WAITING_PROGRESS,
    PROGRESS,
    WAITING_DONE,
    DONE,
    STOPPED
}

internal class ProgressButtonPresenter(private val view: ProgressButton) {
    var state: State = State.BEFORE_DRAW
    
    fun morphStart() {
        view.run {
            hideInitialState()
            setClickable(false)
            setCompoundDrawables(null, null, null, null)
        }

        state = State.MORPHING
    }

    fun morphEnd() {
        if (state == State.WAITING_DONE) {
            state = State.DONE
            Handler().postDelayed({ view.startRevealAnimation() }, 50)
        } else {
            state = State.PROGRESS
        }
    }

    fun morphRevertStart() {
        view.setClickable(false)
        state = State.MORPHING
    }

    fun morphRevertEnd() {
        view.setClickable(true)
        view.recoverInitialState()
        state = State.IDLE
    }

    fun onDraw(canvas: Canvas) {
        if (state == State.BEFORE_DRAW) {
            state = State.IDLE
            view.saveInitialState()
        }

        when (state) {
            State.WAITING_PROGRESS -> view.startMorphAnimation()
            State.PROGRESS -> view.drawProgress(canvas)
            State.DONE -> view.drawDoneAnimation(canvas)
            else -> return
        }
    }

    fun startAnimation() {
        if (state == State.BEFORE_DRAW) {
            state = State.WAITING_PROGRESS
            return
        }

        if (state != State.IDLE) {
            return
        }

        view.startMorphAnimation()
    }

    fun stopAnimation() {
        if (state == State.PROGRESS) {
            view.stopProgressAnimation()
        }

        state = State.STOPPED
    }

    fun revertAnimation() {
        when (state) {
            State.MORPHING -> {
                view.stopMorphAnimation()
                view.startMorphRevertAnimation()
            }
            State.PROGRESS -> {
                view.stopProgressAnimation()
                view.startMorphRevertAnimation()
            }
            State.WAITING_DONE, State.DONE -> {
                view.startMorphRevertAnimation()
            }
            else -> return
        }
    }

    fun doneLoadingAnimation(fillColor: Int, bitmap: Bitmap) {
        view.doneFillColor = fillColor
        view.doneImage = bitmap

        state = when (state) {
            State.PROGRESS -> {
                view.stopProgressAnimation()
                view.startRevealAnimation()
                State.DONE
            }
            State.MORPHING -> State.WAITING_DONE
            else -> State.DONE
        }
    }
}
