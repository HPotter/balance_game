default: extension.zip

extension.zip: extension.dex extension.json
	zip -r extension.zip Theme extension.dex extension.json

extension.dex: ../bin/*.jar
	/home/hpotter/android_sdk/android-sdk-linux/build-tools/17.0.0/dx --dex --output=extension.dex $^

push: extension.zip
	adb push extension.zip /sdcard/

install: clean push
	adb shell am start com.hexonxons.core/.MainActivity

clean:
	rm extension.zip extension.dex
