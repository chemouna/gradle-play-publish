package com.mounacheikhna.publish

import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

/**
 * Created by cheikhnamouna on 2/6/16.
 */
public class CheckTask extends DefaultTask implements CheckSpec {

  private String playFilePath
  private ApplicationVariant variant

  @TaskAction
  void check() {
    //what can we check for ?
    //lets first save a version code

    //TODO: get the version code in play.save
    int oldVersionCode = Integer.parseInt(new File(playFilePath).text)
    def newVersionCode = variant.mergedFlavor.versionCode
    //get current project version code
    if(oldVersionCode >= newVersionCode) {
      throw new StopExecutionException("Version code is less or equal to the play store current version: Play Store " +
              "requires a superior version for each upload.")
    }
  }

  @Override
  void playFilePath(String playFilePath) {
    this.playFilePath = playFilePath
  }

  @Override
  void variant(ApplicationVariant variant) {
    this.variant = variant
  }
}
