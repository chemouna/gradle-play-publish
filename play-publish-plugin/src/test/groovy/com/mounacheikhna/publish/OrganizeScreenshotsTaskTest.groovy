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
    project.tasks.organizeScreenshotsRelease.execute()

  }

}
