
[ ![Download](https://api.bintray.com/packages/lehen01/maven/loading-button/images/download.svg) ](https://bintray.com/lehen01/maven/loading-button/_latestVersion)[![Build Status](https://travis-ci.org/leandroBorgesFerreira/LoadingButtonAndroid.svg?branch=master)](https://travis-ci.org/leandroBorgesFerreira/LoadingButtonAndroid)

# Progress Button Android

![enter image description here](https://i.stack.imgur.com/8SHR1.gif)

Android Button that morphs into a loading progress bar.

  - Fully customizable in the XML
  - Really simple to use.
  - Makes your app looks cooler =D

You can check how this library was implemented here (Old version): https://medium.com/p/9efee6e39711/

## Contents

 - [Installation](#installation)
 - [How to use / Sample](#how-to-use)
	 - [Animate and revert animation](#animate-and-revert-animation)
	 -  [Show done animation](#show-done-animation)
	 - [Revert the loading animation with different text or image](#revert-the-loading-animation-with-different-text-or-image)
 - [Configure XML](#configure-xml)
 - [Avoid Memory Leaks](#avoid-memory-leaks)
 - [Be Creative](#be-creative)
 - [Bugs and feedback](#bugs-and-feedback)
 - [Credits](#credits)

## Installation 

    implementation 'br.com.simplepass:loading-button-android:2.0.6

## How to use

### Animate and revert animation

Add the button in your layout file and customize it the way you like it.

      <br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton
        	    android:id="@+id/btn_id"
        	    android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@drawable/circular_border_shape"
                app:spinning_bar_width="4dp" <!-- Optional -->
                app:spinning_bar_color="#FFF" <!-- Optional -->
                app:spinning_bar_padding="6dp" <!-- Optional -->

Then, instanciate the button

        CircularProgressButton btn = (CircularProgressButton) findViewById(R.id.btn_id)

        btn.startAnimation();

    [do some async task. When it finishes]
    //You can choose the color and the image after the loading is finished
		btn.doneLoadingAnimation(fillColor, bitmap);
		[or just revert de animation]
		btn.revertAnimation();

### Switch to determinant progress
You can switch between indeterminant and determinant progress:

    circularProgressButton.setProgress(10)
    ...
    circularProgressButton.setProgress(50)
    ...
    circularProgressButton.setProgress(100)
    ...

### - Show 'done' animation

When the loading animation is running, call:

    //Choose the color and the image that will be show
    circularProgressButton.doneLoadingAnimation(fillColor, bitmap);

### - Revert the loading animation with different text or image

    progressButton.revertAnimation {
        progressButton.text = "Some new text"
    }
or

    progressImageButton.revertAnimation {
        progressImageButton.setImageResource(R.drawable.image)
    }

## Configure XML

 - app:spinning_bar_width : Changes the width of the spinning bar inside the button
 - app:spinning_bar_color: Changes the color of the spinning bar inside the button
 - app:spinning_bar_padding: Changes the padding of the spinning bar in relation of the button bounds.
 - app:initialCornerAngle: The initial corner angle of the animation. Insert 0 if you have a square button.
 - app:finalCornerAngle: The final corner angle of the animation.

## Avoid Memory Leaks
To avoid memory leaks is your code, you must dispose the buttons in the onDestroy method. Example:

    override fun onDestroy() {
            super.onDestroy()

            progressButton.dispose()
     }

## Bugs and Feedback

For bugs, feature requests, and discussion please use [GitHub Issues](https://github.com/leandroBorgesFerreira/LoadingButtonAndroid/issues).

## Credits

This library was inspired in this repo: https://github.com/dmytrodanylyk/android-morphing-button

### And that's it! Enjoy!
