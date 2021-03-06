package com.mounacheikhna.publish

import com.android.annotations.Nullable
import groovy.io.FileType
import org.apache.commons.lang3.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskAction

public class OrganizeScreenshotsTask extends DefaultTask implements OrganizeScreenshotsSpec {

    private static final String PHONE = "phone"
    private static final String SEVEN_INCH = "sevenInch"
    private static final String TEN_INCH = "tenInch"

    private List<Device> devices
    private String[] locales
    private String screenshotsSource
    private String phone
    private String sevenInch
    private String tenInch
    private String playFolder = "src/main/play"

    @TaskAction
    void performTask() {
        createDevices()
        File screenshotsFolder = new File("${project.projectDir}/$screenshotsSource")
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
        project.tasks.create("copy${file.name}", Copy) {
            from file.path
            into dir
            rename "(.*)_($locale)_(.*).png", '$3.png'
        }.execute()
    }

    String playDeviceDir(Device deviceDetails, String localeFolder) {
        //TODO: this all should be configurable
        def listingFolder = "listing"
        def phoneFolder = "phoneScreenshots"
        def sevenInchFolder = "sevenInchScreenshots"
        def tenInchFolder = "tenInchScreenshots"

        def playImagesDir = "${project.getProjectDir()}/$playBaseFolder/$localeFolder"
        if (deviceDetails.type == PHONE) {
            playImagesDir += "/$listingFolder/$phoneFolder"
        } else if (deviceDetails.type == SEVEN_INCH) {
            playImagesDir += "/$listingFolder/$sevenInchFolder"
        } else if (deviceDetails.type == TEN_INCH) {
            playImagesDir += "/$listingFolder/$tenInchFolder"
        }
        playImagesDir
    }

    private void createDevices() {
        devices = new ArrayList<>(3)
        addDevice(phone, PHONE)
        addDevice(sevenInch, SEVEN_INCH)
        addDevice(tenInch, TEN_INCH)
    }

    private void addDevice(String type, String name) {
        if (type != null && !type.empty) {
            devices.add(new Device(name, type))
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

    @Override
    void playFolder(String playFolder) {
        this.playFolder = playFolder
    }

    static class Device {
        String type //TODO: make device type an enum with some methods encapsulated in it
        String serialNo

        Device(String type, String serialNo) {
            this.type = type
            this.serialNo = serialNo
        }
    }


}
