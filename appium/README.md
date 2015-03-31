# DesignOverlay UI Test

## Set up

Install sauce labs client library:

```shell
pip install sauceclient
```

Install appium client library:

```shell
pip install Appium-Python-Client
pip install pytest
```

## how to run (SauceLabs)
To see logging statements as they are executed, pass the -s flag to py.test.
For configuration, look at the config_sauce_labs.json.example.

```shell
py.test -s appium/android_sauce_labs.py
```