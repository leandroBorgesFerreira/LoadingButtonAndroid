package com.github.leandroborgesferreira.loadingbutton.presentation

import android.graphics.Bitmap
import android.graphics.Canvas
import com.github.leandroborgesferreira.loadingbutton.customViews.ProgressButton
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
class ProgressButtonPresenterTest {

    private lateinit var view: ProgressButton
    private lateinit var canvas: Canvas

    @Before
    fun setUp() {
        view = mock()
        canvas = mock()
    }

    @Test
    fun `should handle state correctly`() {
        ProgressButtonPresenter(view).run {
            assertEquals(State.BEFORE_DRAW, state)

            onDraw(mock())
            assertEquals(State.IDLE, state)
        }
    }

    @Test
    fun `it should call view correctly at given state`() {
        ProgressButtonPresenter(view).run {
            state = State.PROGRESS
            onDraw(canvas)
            verify(view).drawProgress(canvas)
            state = State.DONE
            onDraw(canvas)
            verify(view).drawDoneAnimation(canvas)
        }
    }

    @Test
    fun `view should be clean before morph starts`() {
        ProgressButtonPresenter(view).run {
            morphStart()
            assertEquals(State.MORPHING, state)
        }

        verify(view).run {
            setClickable(false)
            hideInitialState()
            setCompoundDrawables(null, null, null, null)
        }
    }

    @Test
    fun `state should change when morph ends`() {
        ProgressButtonPresenter(view).run {
            morphEnd()
            assertEquals(State.PROGRESS, state)
        }
    }

    @Test
    fun `reveal animation should start if done is called during morph`() {
        ProgressButtonPresenter(view).run {
            morphStart()
            doneLoadingAnimation(1, mock())
            morphEnd()
        }

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        verify(view).startRevealAnimation()
    }

    @Test
    fun `morph revert start should behave correctly`() {
        ProgressButtonPresenter(view).run {
            morphRevertStart()
            assertEquals(State.MORPHING, state)

            verify(view).setClickable(false)
        }
    }

    @Test
    fun `morph revert end should behave correctly`() {
        ProgressButtonPresenter(view).run {
            morphRevertEnd()
            assertEquals(State.IDLE, state)
        }

        verify(view).run {
            setClickable(true)
            recoverInitialState()
        }
    }

    @Test
    fun `view animation start should only be called in Idle state`() {
        ProgressButtonPresenter(view).run {
            State.values()
                .filter { state -> state != State.IDLE }
                .forEach { state ->
                    this.state = state
                    startAnimation()
                }

            verify(view, never()).startMorphAnimation()

            this.state = State.IDLE
            startAnimation()
            verify(view).startMorphAnimation()
        }
    }

    @Test
    fun `when animation is called before the onDraw method, it should call startAnimation on onDraw`() {
        ProgressButtonPresenter(view).run {
            state = State.BEFORE_DRAW
            startAnimation()
            verify(view, never()).startMorphAnimation()
            onDraw(canvas)
            verify(view).startMorphAnimation()
        }
    }

    @Test
    fun `stop animation should call view if presenter is on progress state`() {
        ProgressButtonPresenter(view).run {
            state = State.PROGRESS
            stopAnimation()
            assertEquals(State.STOPPED, state)
        }

        verify(view).stopProgressAnimation()
    }

    @Test
    fun `stop animation should not call view if presenter is NOT on progress state`() {
        ProgressButtonPresenter(view).run {
            stopAnimation()
            assertEquals(State.STOPPED, state)
        }

        verify(view, never()).stopProgressAnimation()
    }

    @Test
    fun `revert animation should do nothing if not in correct stage`() {
        ProgressButtonPresenter(view).run {
            State.values().filterNot { state ->
                state == State.PROGRESS ||
                    state == State.MORPHING ||
                    state == State.DONE ||
                    state == State.WAITING_DONE ||
                    state == State.STOPPED
            }.forEach { state ->
                this.state = state
                revertAnimation()
            }
        }

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `revertAnimation should call correct method of view in morphing state`() {
        ProgressButtonPresenter(view).run {
            state = State.MORPHING
            revertAnimation()
        }

        verify(view).stopMorphAnimation()
        verify(view).startMorphRevertAnimation()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `revertAnimation should call correct method of view in progress state`() {
        ProgressButtonPresenter(view).run {
            state = State.PROGRESS
            revertAnimation()
        }

        verify(view).stopProgressAnimation()
        verify(view).startMorphRevertAnimation()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `revertAnimation should call correct method of view in done state`() {
        ProgressButtonPresenter(view).run {
            state = State.DONE
            revertAnimation()
        }

        verify(view).startMorphRevertAnimation()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `it should save state on the first onDraw`() {
        ProgressButtonPresenter(view).onDraw(mock())
        verify(view).saveInitialState()
    }

    @Test
    fun `onDraw - on progress`() {
        ProgressButtonPresenter(view).run {
            state = State.PROGRESS
            onDraw(mock())
        }

        verify(view).drawProgress(any())
    }

    @Test
    fun `onDraw - done`() {
        ProgressButtonPresenter(view).run {
            state = State.DONE
            onDraw(mock())
        }

        verify(view).drawDoneAnimation(any())
    }

    @Test
    fun `done animation should work if button is on progress state`() {
        ProgressButtonPresenter(view).run {
            state = State.PROGRESS
            doneLoadingAnimation(0, mock())
            assertEquals(State.DONE, state)
        }

        verify(view).run {
            stopProgressAnimation()
            startRevealAnimation()
        }
    }

    @Test
    fun `done animation should only interact with view on Progress state`() {
        val bitmapMock: Bitmap = mock()

        ProgressButtonPresenter(view).run {
            State.values()
                .filter { state ->
                    state != State.PROGRESS
                }.forEach { state ->
                    this.state = state
                    doneLoadingAnimation(0, bitmapMock)
                }
        }

        verify(view, never()).run {
            stopProgressAnimation()
            startRevealAnimation()
        }
    }

    @Test
    fun `if done animation is called before morph finishes, it should not show progress`() {
        ProgressButtonPresenter(view).run {
            startAnimation()
            doneLoadingAnimation(0, mock())

            assertEquals(State.DONE, state) // Should not be progress
        }
    }
}
