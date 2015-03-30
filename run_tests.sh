#!/bin/bash

echo "Uploading debug apk to SauceLabs..."

res=`curl -w %{http_code} -u $SAUCE_USERNAME:$SAUCE_ACCESS_KEY -X POST "http://saucelabs.com/rest/v1/storage/$SAUCE_USERNAME/design_overlay.apk?overwrite=true" \
 -H "Content-Type: application/octet-stream" --data-binary @app/build/outputs/apk/app-debug.apk`

if [ $res -eq 200 ]
then
    echo "APK Uploaded..."
else
    echo "APK Upload failed..."
    exit 1
fi

echo "Starting tests..."

py.test -s appium/android_sauce_labs.py