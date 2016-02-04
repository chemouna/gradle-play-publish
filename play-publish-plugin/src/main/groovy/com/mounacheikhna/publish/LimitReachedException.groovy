package com.mounacheikhna.publish

class LimitReachedException extends IllegalArgumentException {

    private String message

    LimitReachedException(File file, int limit) {
        String place = file.parentFile.parentFile.name + " -> " + file.name;
        message = "File \'" + place + "\' has reached the limit of " + limit + " characters."
    }

    @Override
    String getMessage() {
        return message;
    }
}
