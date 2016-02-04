package com.mounacheikhna.publish

import org.junit.Assert
import org.junit.Test

//@Ignore
class TaskHelperTest {

    private static final File TESTFILE = new File("src/test/fixtures/android_app/src/main/play/en-US/whatsnew")

    @Test
    public void testFilesAreCorrectlyTrimmed() {
        def trimmed = TaskHelper.readAndTrimFile(TESTFILE, 6, false)

        Assert.assertEquals(6, trimmed.length())
    }

    @Test
    public void testShortFilesAreNotTrimmed() {
        def trimmed = TaskHelper.readAndTrimFile(TESTFILE, 100, false)

        Assert.assertEquals(12, trimmed.length())
    }

    @Test
    public void testCorrectTextLength() {
        TaskHelper.readAndTrimFile(TESTFILE, 50, true)
    }

    @Test(expected = LimitReachedException.class)
    public void testIncorrectTextLength() {
        TaskHelper.readAndTrimFile(TESTFILE, 1, true)
    }
}
