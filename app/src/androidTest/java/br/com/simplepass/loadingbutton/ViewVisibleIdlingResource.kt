package br.com.simplepass.loadingbutton

import android.view.View
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback

/*
    IdlingResource that waits until the specified view matches the expected visibility criteria
    Converted to Kotlin from original implementation found here: https://stackoverflow.com/a/35080198
 */
class ViewVisibilityIdlingResource(private val view: View, private val expectedVisibility: Int) : IdlingResource {

    private var mIdle: Boolean = false
    private var mResourceCallback: ResourceCallback? = null

    init {
        this.mIdle = false
        this.mResourceCallback = null
    }

    override fun getName(): String {
        return ViewVisibilityIdlingResource::class.java.simpleName
    }

    override fun isIdleNow(): Boolean {
        mIdle = mIdle || view.getVisibility() === expectedVisibility

        if (mIdle) {
            if (mResourceCallback != null) {
                mResourceCallback!!.onTransitionToIdle()
            }
        }

        return mIdle
    }

    override fun registerIdleTransitionCallback(resourceCallback: ResourceCallback) {
        mResourceCallback = resourceCallback
    }

}