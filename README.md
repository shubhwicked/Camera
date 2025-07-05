Android Camera app based on GpuImage plus. Apply beauty and filters over the camera and capture image. You can use the dependency in your project as well.

## New Features

* Added a built in monochrome filter. Move the beauty slider to the last position to apply the effect.

## Updates

* Project now targets Android SDK 34.
* Runtime checks cover camera and storage permissions including the newer `READ_MEDIA_*` permissions on Android 13+.


want use my project

```
allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
	}	


```
dependency for app level **build.gradle**
```
dependencies {
                implementation 'com.github.shubhwicked:Camera:94c4764'
        }
```	
