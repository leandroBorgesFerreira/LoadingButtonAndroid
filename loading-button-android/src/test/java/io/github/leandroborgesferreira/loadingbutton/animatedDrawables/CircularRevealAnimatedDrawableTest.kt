package io.github.leandroborgesferreira.loadingbutton.animatedDrawables

import android.content.Context
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import io.github.leandroborgesferreira.loadingbutton.R
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CircularRevealAnimatedDrawableTest {

    @Test
    fun `isRunning should return correct state`() {
        val context: Context = ApplicationProvider.getApplicationContext()

        getCircularRevealAnimatedDrawable(context).run {
            assertFalse(isRunning)

            start()
            assertTrue(isRunning)

            stop()
            assertFalse(isRunning)
        }
    }

    @Test
    fun `call start or stop twice should not break anything`() {
        val context: Context = ApplicationProvider.getApplicationContext()

        getCircularRevealAnimatedDrawable(context).run {
            assertFalse(isRunning)

            start()
            start()
            assertTrue(isRunning)

            stop()
            stop()
            assertFalse(isRunning)
        }
    }

    private fun getCircularRevealAnimatedDrawable(context: Context) =
        CircularRevealAnimatedDrawable(
            mock(),
            android.R.color.black,
            BitmapFactory.decodeResource(context.resources, R.drawable.abc_ab_share_pack_mtrl_alpha))
}
