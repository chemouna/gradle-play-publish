package com.mounacheikhna.publish

import org.gradle.api.Project
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Created by cheikhnamouna on 2/4/16.
 */
//@Ignore
public class OrganizeScreenshotsTaskTest {

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void imageIsCopiedToFolderUsingNameConvention() {
    Project project = TestHelper.evaluatableProject()

    project.evaluate()
    project.tasks.clean.execute()

    OrganizeScreenshotsTask task = project.tasks.organizeScreenshotsRelease
    task.locales = ["en_US", "fr_FR", "es_ES"]
    task.screenshotsSource = "screenshotsOutput"
    task.phone = "041e4f"
    task.sevenInch = "059adef"

    project.tasks.organizeScreenshotsRelease.execute()

    /*
     - apply plugin with uploadImages = true
     - format "$device_serial_nb$locale$name" or maybe better "phone$locale$name"
          phone -> 041e4f7325232e3c
          tablet -> 059adef

          params :
          need to be passed either here or via extension ?
     */
  }

}
