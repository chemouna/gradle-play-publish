package com.mounacheikhna.publish

import com.android.build.gradle.api.ApplicationVariant
import com.google.api.services.androidpublisher.model.Apk
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

/**
 * Created by m.cheikhna on 07/02/2016.
 */
class CheckPublishTask extends PublishTask implements CheckSpec {

    private String playFilePath
    private ApplicationVariant variant

    @TaskAction
    check() {
        super.publish()
        checkVersionCode()
    }

    def checkVersionCode() {
        List<Apk> apks = edits.apks()
                .list(variant.applicationId, editId)
                .execute()
                .getApks()
        if (apks == null) {
            return
        }

        Integer versionCode = apks.collect { apk -> apk.getVersionCode() }.max()
        if(versionCode <= variant.mergedFlavor.versionCode) {
            throw new StopExecutionException("Play Store requires a higher version code to upload a new apk.")
        }
    }

    @Override
    public void playFilePath(String playFilePath) {
        this.playFilePath = playFilePath;
    }

    @Override
    public void variant(ApplicationVariant variant) {
        this.variant = variant;
    }
}
