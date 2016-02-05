package com.mounacheikhna.publish

import com.android.build.gradle.api.ApkVariantOutput
import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.model.Apk
import com.google.api.services.androidpublisher.model.ApkListing
import com.google.api.services.androidpublisher.model.Track
import org.gradle.api.tasks.TaskAction

class PublishApkTask extends PublishTask {

  static def MAX_CHARACTER_LENGTH_FOR_WHATS_NEW_TEXT = 500
  static def FILE_NAME_FOR_WHATS_NEW_TEXT = "whatsnew"

  File inputFolder

  @TaskAction
  publishApk() {
    super.publish()
    Apk apk = uploadApk()
    updateTrack(apk)
    updateListing(apk)
    edits.commit(variant.applicationId, editId).execute()
  }

  private void updateTrack(Apk apk) {
    Track newTrack = new Track().setVersionCodes([apk.getVersionCode()])
    if (extension.track?.equals("rollout")) {
      newTrack.setUserFraction(extension.userFraction)
    }
    edits.tracks()
            .update(variant.applicationId, editId, extension.track, newTrack)
            .execute()
  }

  private Apk uploadApk() {
    def apkOutput = variant.outputs.find {
      variantOutput -> variantOutput instanceof ApkVariantOutput
    }
    FileContent newApkFile = new FileContent(PublisherHelper.MIME_TYPE_APK, apkOutput.outputFile)

    Apk apk = edits.apks()
            .upload(variant.applicationId, editId, newApkFile)
            .execute()
    apk
  }

  private void updateListing(Apk apk) {
    if (!inputFolder.exists()) return

    // Matches if locale have the correct naming e.g. en-US for play store
    inputFolder.eachDirMatch(matcher) { dir ->
      File whatsNewFile = new File(dir, FILE_NAME_FOR_WHATS_NEW_TEXT + "-" + extension.track)

      if (!whatsNewFile.exists()) {
        whatsNewFile = new File(dir, FILE_NAME_FOR_WHATS_NEW_TEXT)
      }

      if (whatsNewFile.exists()) {
        def whatsNewText = TaskHelper.readAndTrimFile(whatsNewFile,
                MAX_CHARACTER_LENGTH_FOR_WHATS_NEW_TEXT, extension.errorOnSizeLimit)
        def locale = dir.name

        ApkListing newApkListing = new ApkListing().setRecentChanges(whatsNewText)
        edits.apklistings().
                update(variant.applicationId, editId, apk.getVersionCode(), locale, newApkListing)
                .execute()
      }
    }
  }
}
