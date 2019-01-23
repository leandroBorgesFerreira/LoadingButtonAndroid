package br.com.simplepass.loadingbutton.presentation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import br.com.simplepass.loadingbutton.customViews.ProgressButton

enum class State {
    BEFORE_DRAW,
    IDLE,
    MORPHING,
    MORPHING_REVERT,
    WAITING_PROGRESS,
    PROGRESS,
    WAITING_DONE,
    DONE,
    WAITING_TO_STOP,
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
        state = when (state) {
            State.WAITING_DONE -> {
                Handler().postDelayed({ view.startRevealAnimation() }, 50)
                State.DONE
            }
            State.WAITING_TO_STOP -> State.STOPPED
            else -> State.PROGRESS
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
        state = when (state) {
            State.PROGRESS -> {
                view.stopProgressAnimation()
                State.STOPPED
            }
            State.MORPHING, State.WAITING_PROGRESS -> State.WAITING_TO_STOP
            else -> State.STOPPED
        }
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
            State.WAITING_DONE, State.STOPPED, State.DONE -> {
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
            State.STOPPED -> {
                view.startRevealAnimation()
                State.DONE
            }
            else -> State.DONE
        }
    }

    internal fun validateSetProgress(): Boolean =
        state == State.PROGRESS || state == State.MORPHING || state == State.WAITING_PROGRESS
}
