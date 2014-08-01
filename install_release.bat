@rem install and run release apk on connected device
@echo Installing app...
@echo off
adb uninstall io.piotrjastrzebski.sfg.android
adb install -r android/android-release.apk
adb shell am start -n io.piotrjastrzebski.sfg.android/.AndroidLauncher
@echo on