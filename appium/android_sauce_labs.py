#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""

Author : Manabu Shimobe

"""
__author__ = "Manabu Shimobe"

from appium import webdriver
from appium import SauceTestCase, on_platforms

from time import sleep
from logging import getLogger, StreamHandler, Formatter, DEBUG
from os import environ
import json

# load default platform configurations
json_file = open('appium/config_sauce_labs.json')
platforms = json.load(json_file)
for platform in platforms:
    platform['app'] = "sauce-storage:%s" % environ.get('SAUCE_APK_FILE')
    platform['customData'] = {'commit': environ.get('TRAVIS_COMMIT', environ.get('SAUCE_COMMIT')),
                              'versionName': environ.get('SAUCE_APK_VERSION_NAME'),
                              'versionCode': environ.get('SAUCE_APK_VERSION_CODE')}
    platform['build'] = "build-%s" % environ.get('TRAVIS_BUILD_NUMBER', 'local')
json_file.close()

# set up logger
logger = getLogger(__name__)
logger.setLevel(DEBUG)
handler = StreamHandler()
handler.setFormatter(Formatter('%(asctime)s- %(name)s - %(levelname)s - %(message)s'))
handler.setLevel(DEBUG)
logger.addHandler(handler)

# the emulator is sometimes slow
SLEEP_TIME = 1

@on_platforms(platforms)
class SimpleAndroidSauceTests(SauceTestCase):
    
    def test_settings(self):
        sleep(SLEEP_TIME)
        
        # Check if successfully started SettingsActivity
        self.assertEqual('.activity.SettingsActivity_', self.driver.current_activity)

        el_switch = self.driver.find_element_by_accessibility_id('Grid Switch')
        self.assertIsNotNone(el_switch)

        # Grid should be shown now
        el_switch.click()
        logger.info('Clicked Grid Switch')

        sleep(SLEEP_TIME)