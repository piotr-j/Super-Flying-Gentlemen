# Super Flying Gentlemen
SFG is a action packed game designed for mobile devices. It was inspired by Flappy Bird.

You can get Android version on [Google Play](https://play.google.com/store/apps/details?id=io.piotrjastrzebski.sfg.android)

Read more about it on [my site](http://piotrjastrzebski.io/project/super-flying-gentleman/)

![Super Flying Gentlemen screen shot](http://piotrjastrzebski.io/img/sfg1.png)

## Building and running the game
### Importing and running the project

This project is Gradle based, valid Gradle installation is required.
Consult [libGDX wiki](https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse%2C-Intellij-IDEA%2C-NetBeans%29) to learn how to do that.

1. clone this repo
git clone ...

2. import project into your ide

### Running desktop version
Create run config with DesktopLauncher as main class. 
Make sure that working directory is set to ./android/assets 
Run the project.

### Running HTML version
Run ./gradlew html:dist upload html/build/dist somewhere or python -m SimpleHTTPServer 8080 in that directory. WEB-INF directory is not required. 

### Running Android version
Running Android version requires additional setup. The app uses various Google services such as billing, ads, achievements and leader boards.
They must be configured to test them, or disabled by removing relevant calls from AndroidActionResolver. Required strings are located in android/res/values/strings.xml

## Used tools
SFG uses following tools:
[libGDX](http://libgdx.badlogicgames.com/) - multi-platform game framework

[Spine](http://esotericsoftware.com/) - 2d animation framework

Various Google libs for Android and GWT
