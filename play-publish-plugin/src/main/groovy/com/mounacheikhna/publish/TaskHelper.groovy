package com.mounacheikhna.publish

import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.FileContent

class TaskHelper {

    def static readAndTrimFile(File file, int maxCharLength, boolean errorOnSizeLimit) {
        if (file.exists()) {
            def message = file.text
            if (message.length() > maxCharLength) {
                if (errorOnSizeLimit) {
                    throw new LimitReachedException(file, maxCharLength)
                }

                return message.substring(0, maxCharLength)
            }
            return message
        }

        return ""
    }

    def static List<AbstractInputStreamContent> getImageListAsStream(File listingDir, String graphicPath) {
        File graphicDir = new File(listingDir, graphicPath)
        if (graphicDir.exists()) {
            return graphicDir.listFiles(new ImageFileFilter()).sort().collect { file ->
                new FileContent(PublisherHelper.MIME_TYPE_IMAGE, file);
            }
        }
        return null
    }

    def static AbstractInputStreamContent getImageAsStream(File listingDir, String graphicPath) {
        File graphicDir = new File(listingDir, graphicPath)
        if (graphicDir.exists()) {
            File[] files = graphicDir.listFiles(new ImageFileFilter())
            if (files.length > 0) {
                File graphicFile = files[0]
                return new FileContent(PublisherHelper.MIME_TYPE_IMAGE, graphicFile);
            }
        }
        return null
    }

    static class ImageFileFilter implements FileFilter {
        @Override
        boolean accept(File pathname) {
            return pathname.name.toLowerCase().endsWith(".png") ||
                    pathname.name.toLowerCase().endsWith(".jpg")
        }
    }

}
