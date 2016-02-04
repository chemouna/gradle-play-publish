package com.mounacheikhna.publish

import com.android.build.gradle.api.ApplicationVariant
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.AppEdit
import org.gradle.api.DefaultTask

class PlayPublishTask extends DefaultTask {

    def matcher = ~"^[a-z]{2}(-([A-Z]{2}|419))?\\z"

    PlayPublisherPluginExtension extension

    ApplicationVariant variant

    String editId

    AndroidPublisher service

    AndroidPublisher.Edits edits

    def publish() {
        if (service == null) {
            service = AndroidPublisherHelper.init(extension)
        }

        edits = service.edits()

        AndroidPublisher.Edits.Insert editRequest = edits.insert(variant.applicationId, null)

        try {
            AppEdit edit = editRequest.execute()
            editId = edit.getId()
        } catch (GoogleJsonResponseException e) {

            if (e.message != null && e.message.contains("applicationNotFound")) {
                throw new IllegalArgumentException("No application was found for the package name " + variant.applicationId + ". Is this the first release for this app? The first version has to be uploaded via the web interface.", e);
            }

            throw e;
        }
    }

}
