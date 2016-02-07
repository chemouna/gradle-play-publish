package com.mounacheikhna.publish

interface OrganizeScreenshotsSpec {

    void screenshotsSource(String dir)
    void phone(String phone) //how about something like phone(String phone, String folder)
    void sevenInch(String sevenInch)
    void tenInch(String tenInch)
    void locales(String[] locales)
    void playFolder(String playFolder)

}
