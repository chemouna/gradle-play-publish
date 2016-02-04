package com.mounacheikhna.publish

import junit.framework.TestCase
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

//@Ignore
class createPlayFilesTest {

    @Test
    public void testResourcesAreCopiedIntoOutputFolder() {
        Project project = TestHelper.evaluatableProject()

        project.evaluate()

        project.tasks.clean.execute()
        project.tasks.generateReleasePlayResources.execute()

        Assert.assertTrue(new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play").exists())
        Assert.assertTrue(new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/release").exists())
        Assert.assertTrue(new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/release/en-US").exists())
        Assert.assertTrue(new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/release/fr-FR").exists())

        String content = FileUtils.readFileToString(
                new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/release/en-US/whatsnew"))
        TestCase.assertEquals("main english", content)
        content = FileUtils.readFileToString(
                new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/release/fr-FR/whatsnew"))
        TestCase.assertEquals("main french", content)
    }

    @Test
    public void testFlavorsOverrideMain() {
        Project project = TestHelper.evaluatableProject()

        project.android.productFlavors {
            free
            paid
        }

        project.evaluate()

        project.tasks.clean.execute()
        project.tasks.generateFreeReleasePlayResources.execute()

        Assert.assertTrue(new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play").exists())
        Assert.assertTrue(new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/freeRelease").exists())
        Assert.assertTrue(new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/freeRelease/de-DE").exists())
        Assert.assertTrue(new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/freeRelease/en-US").exists())
        Assert.assertTrue(new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/freeRelease/fr-FR").exists())

        String content = FileUtils.readFileToString(
                new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/freeRelease/de-DE/whatsnew"))
        TestCase.assertEquals("free german", content)
        content = FileUtils.readFileToString(
                new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/freeRelease/fr-FR/whatsnew"))
        TestCase.assertEquals("main french", content)
        content = FileUtils.readFileToString(
                new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/freeRelease/en-US/whatsnew"))
        TestCase.assertEquals("main english", content)

        project.tasks.generatePaidReleasePlayResources.execute()

        content = FileUtils.readFileToString(
                new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/paidRelease/de-DE/whatsnew"))
        TestCase.assertEquals("paid german", content)
        content = FileUtils.readFileToString(
                new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/paidRelease/fr-FR/whatsnew"))
        TestCase.assertEquals("main french", content)
        content = FileUtils.readFileToString(
                new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/paidRelease/en-US/whatsnew"))
        TestCase.assertEquals("paid english", content)
    }

    @Test
    public void testBuildTypeOverridesMain() {

        Project project = TestHelper.evaluatableProject()

        project.android {
            buildTypes {
                dogfood.initWith(buildTypes.release)
            }
        }

        project.evaluate()

        project.tasks.clean.execute()
        project.tasks.generateDogfoodPlayResources.execute()

        String content = FileUtils.readFileToString(
                new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/dogfood/en-US/whatsnew"))
        TestCase.assertEquals("dogfood english", content)
    }

    @Test
    public void testBuildTypeOverridesFlavor() {

        Project project = TestHelper.evaluatableProject()

        project.android {
            productFlavors {
                free
                paid
            }

            buildTypes {
                dogfood.initWith(buildTypes.release)
            }
        }

        project.evaluate()

        project.tasks.clean.execute()
        project.tasks.generatePaidDogfoodPlayResources.execute()

        String content = FileUtils.readFileToString(
                new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/paidDogfood/en-US/whatsnew"))
        TestCase.assertEquals("dogfood english", content)
    }

    @Test
    public void testVariantOverridesBuildType() {

        Project project = TestHelper.evaluatableProject()

        project.android {
            productFlavors {
                free
                paid
            }

            buildTypes {
                dogfood.initWith(buildTypes.release)
            }
        }

        project.evaluate()

        project.tasks.clean.execute()
        project.tasks.generateFreeDogfoodPlayResources.execute()

        String content = FileUtils.readFileToString(
                new File(TestHelper.FIXTURE_WORKING_DIR, "build/outputs/play/freeDogfood/en-US/whatsnew"))
        TestCase.assertEquals("free dogfood english", content)
    }

}
