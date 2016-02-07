package com.mounacheikhna.publish

import com.android.build.gradle.api.ApplicationVariant

/**
 * Created by m.cheikhna on 07/02/2016.
 */
interface CheckSpec {

    void playFilePath(String playFilePath)
    void variant(ApplicationVariant variant)
}
