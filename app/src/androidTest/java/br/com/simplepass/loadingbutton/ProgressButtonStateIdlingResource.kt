package br.com.simplepass.loadingbutton

import androidx.test.espresso.IdlingResource
import br.com.simplepass.loadingbutton.customViews.ProgressButton
import br.com.simplepass.loadingbutton.presentation.State

class ProgressButtonStateIdlingResource(
        private val progressButton: ProgressButton,
        private val expectedState: State = State.IDLE
) : IdlingResource {

    private var idle: Boolean = false
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = progressButton::class.java.simpleName

    override fun isIdleNow(): Boolean {
        idle = idle || progressButton.getState() == expectedState

        if (idle) {
            if (resourceCallback != null) {
                resourceCallback!!.onTransitionToIdle()
            }
        }

        return idle
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }
}