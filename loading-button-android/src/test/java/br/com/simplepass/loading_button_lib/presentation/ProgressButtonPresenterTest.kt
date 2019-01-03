package br.com.simplepass.loading_button_lib.presentation

import android.graphics.Canvas
import br.com.simplepass.loading_button_lib.customViews.ProgressButton
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
class ProgressButtonPresenterTest {

    private val view: ProgressButton = mock()
    private val canvas: Canvas = mock()

    @Test
    fun `should handle state correctly`() {
        ProgressButtonPresenter(view).run {
            assertEquals(state, State.BEFORE_DRAW)

            onDraw(mock())
            assertEquals(state, State.IDLE)
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
            assertEquals(state, State.MORPHING)
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
            assertEquals(state, State.PROGRESS)
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
            assertEquals(state, State.MORPHING)

            verify(view).setClickable(false)
        }
    }

    @Test
    fun `morph revert end should behave correctly`() {
        ProgressButtonPresenter(view).run {
            morphRevertEnd()
            assertEquals(state, State.IDLE)
        }

        verify(view).run {
            setClickable(true)
            recoverInitialState()
        }
    }

    @Test
    fun `view animation start should only be called in Idle state`() {
        ProgressButtonPresenter(view).run {
            state = State.BEFORE_DRAW

        }
    }


}
