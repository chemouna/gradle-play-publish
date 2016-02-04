package com.mounacheikhna.publish

class ImageFileFilter implements FileFilter {
    @Override
    boolean accept(File pathname) {
        return pathname.name.toLowerCase().endsWith(".png") ||
                pathname.name.toLowerCase().endsWith(".jpg")
    }
}
