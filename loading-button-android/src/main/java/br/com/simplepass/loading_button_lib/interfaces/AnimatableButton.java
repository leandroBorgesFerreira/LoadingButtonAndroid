package br.com.simplepass.loading_button_lib.interfaces;

/**
 * Created by hinovamobile on 23/12/16.
 */

public interface AnimatableButton {
    void startAnimation();
    void revertAnimation();
    public void revertAnimation(final OnAnimationEndListener onAnimationEndListener);
}
