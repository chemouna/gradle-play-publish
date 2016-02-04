package com.mounacheikhna.publish

import groovy.io.FileType
import groovyjarjarantlr.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

public class OrganizeScreenshotsTask extends DefaultTask implements OrganizeScreenshotsSpec {

    static final String PLAY_FOLDER = "src/main/play"
    private static final String PHONE = "phone"
    private static final String SEVEN_INCH = "sevenInch"
    private static final String TEN_INCH = "tenInch"

    private List<Device> devices
    private String[] locales
    private String screenshotsSource
    private String phone
    private String sevenInch
    private String tenInch

    @TaskAction
    void performTask() {
        createDevices()
        if (!project.plugins.hasPlugin('android')) {
            throw new StopExecutionException("The 'android' plugin is required.")
        }
        File screenshotsFolder
        screenshotsFolder = new File("${project.projectDir}/$screenshotsSource")
        def allLocales = locales;
        screenshotsFolder.eachFileRecurse(FileType.DIRECTORIES) {
            dir ->
                Device device = getDeviceForDirectory(dir)
                if (device != null) {
                    dir.eachFileRecurse(FileType.FILES) {
                        def index = StringUtils.indexOfAny(it.name, allLocales);
                        if (it.name.contains(".png") && index != -1) {
                            def value = it.name.substring(index, index + 5)
                            def localeFolder = value.replace("_", "-")
                            copyImage(it, playDeviceDir(device, localeFolder), value)
                        }
                    }
                }
        }
    }

    Device getDeviceForDirectory(File dir) {
        def deviceSerialNumber = dir.name.findAll(~/\d+_/).join(".").replace("_", "")
        if (deviceSerialNumber == null || deviceSerialNumber.empty) {
            deviceSerialNumber = dir.name
        }
        this.devices.find({ it.serialNo.contains(deviceSerialNumber) })
    }

    void copyImage(File file, dir, locale) {
        project.tasks.create(copy${file.name}, Copy) {
            from file.path
            into dir
            rename "(.*)_($locale)_(.*).png", '$3.png'
        }.execute()
    }

    String playDeviceDir(Device deviceDetails, String localeFolder) {
        def playImagesDir = "${project.getProjectDir()}/$PLAY_FOLDER/$localeFolder/listing/"
        if (deviceDetails.type == PHONE) {
            playImagesDir += "phoneScreenshots"
        } else if (deviceDetails.type == SEVEN_INCH) {
            playImagesDir += "sevenInchScreenshots"
        } else if (deviceDetails.type == TEN_INCH) {
            playImagesDir += "tenInchScreenshots"
        }
        playImagesDir
    }

    private void createDevices() {
        devices = new ArrayList<>(3)
        if (phone != null && !phone.empty) {
            devices.add(new Device(PHONE, phone))
        }
        if (sevenInch != null && !sevenInch.empty) {
            devices.add(new Device(SEVEN_INCH, sevenInch))
        }
        if (tenInch != null && !tenInch.empty) {
            this.devices.add(new Device(TEN_INCH, tenInch))
        }
    }

    @Override
    void screenshotsSource(String dir) {
        this.screenshotsSource = dir
    }

    @Override
    void phone(String phone) {
        this.phone = phone
    }

    @Override
    void sevenInch(String sevenInch) {
        this.sevenInch = sevenInch
    }

    @Override
    void tenInch(String tenInch) {
        this.tenInch = tenInch
    }

    @Override
    void locales(String[] locales) {
        this.locales = locales
    }

    static class Device {
        String type
        String serialNo

        Device(String type, String serialNo) {
            this.type = type
            this.serialNo = serialNo
        }
    }


}
