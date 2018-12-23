package br.com.simplepass.loading_button_lib.interfaces;

public interface AnimatedButton {
    void startAnimation();
    void revertAnimation();
    void revertAnimation(final OnAnimationEndListener onAnimationEndListener);
    void dispose();
    void setProgress(float progress);
    void resetProgress();
}
