package br.com.simplepass.loadingbutton.animatedDrawables

import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CircularProgressAnimatedDrawableTest {

    @Test
    fun `isRunning should return correct state`() {
        getCircularAnimatedDrawable().run {
            assertFalse(isRunning)

            start()
            assertTrue(isRunning)

            stop()
            assertFalse(isRunning)
        }
    }

    @Test
    fun `call start or stop twice should not break anything`() {
        getCircularAnimatedDrawable().run {
            assertFalse(isRunning)

            start()
            start()
            assertTrue(isRunning)

            stop()
            stop()
            assertFalse(isRunning)
        }
    }

    @Test
    fun `setting progress should update the state to determinate`() {
        getCircularAnimatedDrawable().run {
            val newProgress = 20F

            assertEquals(progressType, ProgressType.INDETERMINATE)

            progress = newProgress

            assertEquals(progress, newProgress)
            assertEquals(progressType, ProgressType.DETERMINATE)
        }
    }

    private fun getCircularAnimatedDrawable() = CircularProgressAnimatedDrawable(mock(), 5F, 2)
}
