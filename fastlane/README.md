fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew install fastlane`

# Available Actions
## Android
### android apk
```
fastlane android apk
```
Build a new production version (apk, signed)
### android bundle
```
fastlane android bundle
```
Build a new production version (bundle, signed)
### android playstore
```
fastlane android playstore
```
Build and deploy a new production version to the internal testing track

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
