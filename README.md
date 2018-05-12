# Chameleon Mini Live Debugger
```
  _____ _                          _                  __  __ _       _ 
 / ____| |                        | |                |  \/  (_)     (_)
| |    | |__   __ _ _ __ ___   ___| | ___  ___  _ __ | \  / |_ _ __  _ 
| |    | '_ \ / _` | '_ ` _ \ / _ \ |/ _ \/ _ \| '_ \| |\/| | | '_ \| |
| |____| | | | (_| | | | | | |  __/ |  __/ (_) | | | | |  | | | | | | |
 \_____|_| |_|\__,_|_| |_| |_|\___|_|\___|\___/|_| |_|_|  |_|_|_| |_|_|
 _      _           _____       _                                 
| |    (_)         |  __ \     | |                                
| |     ___   _____| |  | | ___| |__  _   _  __ _  __ _  ___ _ __ 
| |    | \ \ / / _ \ |  | |/ _ \ '_ \| | | |/ _` |/ _` |/ _ \ '__|
| |____| |\ V /  __/ |__| |  __/ |_) | |_| | (_| | (_| |  __/ |   
|______|_| \_/ \___|_____/ \___|_.__/ \__,_|\__, |\__, |\___|_|   
                                             __/ | __/ |          
                                            |___/ |___/           
```
![](https://user-images.githubusercontent.com/22165688/35208704-a1ebdc68-ff17-11e7-8fde-291c89ff61bd.png)

This application is an interactive NFC debugging and logging tool for Android using the [Chameleon Mini](https://github.com/emsec/ChameleonMini) Rev. g boards using [this (microUSB)](https://www.amazon.com/gp/product/B00CXAC1ZW/ref=oh_aui_detailpage_o03_s00?ie=UTF8&psc=1) or [this (USB-C)](https://www.amazon.com/gp/product/B071J92Q91/ref=oh_aui_detailpage_o02_s00?ie=UTF8&psc=1) USB cable (or some variants thereof). The Chameleon Mini is a hardware tool for NFC debugging, card emulation, security testing, reconnaissance, and general purpose debugging over this interface. These small card-sized NFC emulator devices are able to clone contact-less cards and RFID tags and sniff the raw RF-framed data sent over the interface without the overhead of Android's prohibitive NFC stack. Recent versions of the Chameleon boards (Rev. >= g) have the option to perform LIVE logging with the device where the raw logs are printed to the console in real time. This application provides a portable interface to be used with the live logging features of the device for on the go and stealth NFC card reader debugging and testing. This application also provides full support for the [RevE board command set](https://github.com/iceman1001/ChameleonMini-rebooted/wiki/Terminal-Commands) and is a solid choice as an Android-based GUI for the [RevE Rebooted](https://github.com/iceman1001/ChameleonMini-rebooted) firmware variants of these devices.

The application is available in two flavors [free](https://play.google.com/store/apps/details?id=com.maxieds.chameleonminilivedebugger) and [paid](https://play.google.com/store/apps/details?id=com.maxieds.chameleonminilivedebugger.paid) on the Google Play Store. This is probably the easiest and most reliable way to install the application since updates will be automatically applied and installed on the target machines as they are rolled out into production (please use the production releases instead of the artifact beta testing APKs as they are updated much more frequently). Signed APKs (the same ones uploaded to Google) are also provided periodically on the [releases page](https://github.com/maxieds/ChameleonMiniLiveDebugger/releases/latest) for the project.

# Documentation

* See the [Releases page](https://github.com/maxieds/ChameleonMiniLiveDebugger/releases) for current non-development source code snapshots and recent APK downloads
* See the [Wiki main page](https://github.com/maxieds/ChameleonMiniLiveDebugger/wiki) for more information.
* See the [Wiki screenshots page](https://github.com/maxieds/ChameleonMiniLiveDebugger/wiki/Screenshots) (also [Issue #4](https://github.com/maxieds/ChameleonMiniLiveDebugger/issues/4), [Issue #7](https://github.com/maxieds/ChameleonMiniLiveDebugger/issues/7), and [Issue #9](https://github.com/maxieds/ChameleonMiniLiveDebugger/issues/9)) for screenshots 
* See the [Wiki documentation page](https://github.com/maxieds/ChameleonMiniLiveDebugger/wiki/Documentation) for more information about usage of the app

# Licensing and Credits

For all intensive reasonable non-commercial applications this app is free to use and modify provided a link to the original project webpage and the author information remain intact in the revisions (i.e., at minimum a copy of this README.md file and a link to the [source repo](https://github.com/maxieds/ChameleonMiniLiveDebugger) must be included with any re-distribution). Call this licensing scheme GPL-like. The bulk of the pretty stylized freeware icons used by the app were obtained from the [Flat Icon site](flaticon.com) and their lovely sized transparent PNG conversions of these images.

# Donations / Contributing to the Project

The app is freeware so instead of adding a PayPal account for good Samaritan user donations, I will just point out that I would like to have another couple of these devices to play with and loan to friends for another private app I'm developing. If anyone finds this app useful and has the resources to donate an extra Chameleon or two (any revision should do the trick), please contact me. I'd also be interested in obtaining one of the Rev. E boards for testing with the app and some of the Proxmark hardware for coding related applications. There is also a [paid release](https://play.google.com/store/apps/details?id=com.maxieds.chameleonminilivedebugger.paid) available from the Play Store for $5 which includes other features such as color profiles and themes for the UI. The price tag is reasonable given the chunk of change needed to acquire a Chameleon Mini device to run with the app, so please contribute to the development of the project by installing this release if you find the app useful for your GUI logging and device configuration needs.
Additionally, you can contribute to the development of the app by providing [debugging information](https://github.com/maxieds/ChameleonMiniLiveDebugger/issues/1) and [feature requests](https://github.com/maxieds/ChameleonMiniLiveDebugger/issues/2) on the [issues thread](https://github.com/maxieds/ChameleonMiniLiveDebugger/issues) for the project. 
