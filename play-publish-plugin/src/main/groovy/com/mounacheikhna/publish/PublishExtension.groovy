package com.mounacheikhna.publish

class PublishExtension {

    String serviceAccountEmail

    File pk12File

    File jsonFile

    boolean uploadImages = false

    boolean errorOnSizeLimit = true

    private String track = 'alpha'

    void setTrack(String track) { //'alpha', 'beta', 'rollout', 'production'
        if (!(track in ['alpha', 'beta', 'rollout', 'production'])) {
            throw new IllegalArgumentException("Track has to be one of 'alpha', 'beta', 'rollout' or 'production'.")
        }
        this.track = track
    }

    def getTrack() {
        return track
    }

    Double userFraction = 0.1

    String screenshotsSource

}
