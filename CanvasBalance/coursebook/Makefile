default: book.zip

book.zip: data/book_markup.json data/cover.png
	cd data && zip -r ../book.zip .

push: book.zip
	adb push book.zip /sdcard/

install: clean push
	adb shell am start com.hexonxons.core/.MainActivity

clean:
	rm book.zip
