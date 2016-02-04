package com.mounacheikhna.publish

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Ignore

public class TestHelper {

    public static final File FIXTURE_WORKING_DIR = new File("src/test/fixtures/android_app")

    public static Project evaluatableProject() {
        Project project = ProjectBuilder.builder().withProjectDir(FIXTURE_WORKING_DIR).build()
        project.apply plugin: 'com.android.application'
        project.apply plugin: 'com.mounacheikhna.play-publish'
        project.android {
            compileSdkVersion 23
            buildToolsVersion '23.0.1'

            defaultConfig {
                versionCode 1
                versionName '1.0'
                minSdkVersion 23
                targetSdkVersion 23
            }

            buildTypes {
                release {
                    signingConfig signingConfigs.debug
                }
            }
        }

        return project
    }

    public static Project noSigningConfigProject() {
        Project project = ProjectBuilder.builder().withProjectDir(FIXTURE_WORKING_DIR).build()
        project.apply plugin: 'com.android.application'
        project.apply plugin: 'com.mounacheikhna.play-publish'
        project.android {
            compileSdkVersion 23
            buildToolsVersion '23.0.1'

            defaultConfig {
                versionCode 1
                versionName '1.0'
                minSdkVersion 23
                targetSdkVersion 23
            }

            buildTypes {
                release {
                }
            }
        }

        return project
    }
}
