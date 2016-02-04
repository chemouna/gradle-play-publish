package com.mounacheikhna.publish

import org.gradle.api.Project
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import static org.junit.Assert.assertEquals
import org.junit.Ignore;

//@Ignore
class PublishPluginTest {

    @Test(expected = PluginApplicationException.class)
    public void testThrowsOnLibraryProjects() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'com.android.library'
        project.apply plugin: 'com.mounacheikhna.play-publish'
    }

    @Test
    public void testCreatesDefaultTask() {
        Project project = TestHelper.evaluatableProject()
        project.evaluate()

        Assert.assertNotNull(project.tasks.publishRelease)
        Assert.assertEquals(project.tasks.publishApkRelease.variant, project.android.applicationVariants[1])
    }

    @Test
    public void testCreatesFlavorTasks() {
        Project project = TestHelper.evaluatableProject()

        project.android.productFlavors {
            free
            paid
        }

        project.evaluate()

        Assert.assertNotNull(project.tasks.publishPaidRelease)
        Assert.assertNotNull(project.tasks.publishFreeRelease)

        Assert.assertEquals(project.tasks.publishApkFreeRelease.variant, project.android.applicationVariants[3])
        Assert.assertEquals(project.tasks.publishApkPaidRelease.variant, project.android.applicationVariants[1])
    }

    @Test
    public void testDefaultTrack() {
        Project project = TestHelper.evaluatableProject()
        project.evaluate()

        Assert.assertEquals('alpha', project.extensions.findByName("play").track)
    }

    @Test
    public void testTrack() {
        Project project = TestHelper.evaluatableProject()

        project.play {
            track 'production'
        }

        project.evaluate()

        Assert.assertEquals('production', project.extensions.findByName("play").track)
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsOnInvalidTrack() {
        Project project = TestHelper.evaluatableProject()

        project.play {
            track 'gamma'
        }
    }

    @Test
    public void testUserFraction() {
        Project project = TestHelper.evaluatableProject()

        project.play {
            userFraction 0.1
        }

        project.evaluate()

        assertEquals(0.1, project.extensions.findByName("play").userFraction, 0)
    }

    @Test
    public void testJsonFile() {
        Project project = TestHelper.evaluatableProject()

        project.play {
            jsonFile new File("key.json");
        }

        project.evaluate()

        Assert.assertEquals("key.json", project.extensions.findByName("play").jsonFile.name)
    }


    @Test
    public void testPublishListingTask() {
        Project project = TestHelper.evaluatableProject()

        project.android.productFlavors {
            free
            paid
        }

        project.evaluate()

        Assert.assertNotNull(project.tasks.publishListingFreeRelease)
        Assert.assertNotNull(project.tasks.publishListingPaidRelease)
    }

    @Test
    public void testNoSigningConfigGenerateTasks() {
        Project project = TestHelper.noSigningConfigProject()

        project.evaluate()

        Assert.assertNotNull(project.tasks.bootstrapReleasePlayResources)
        Assert.assertNotNull(project.tasks.publishListingRelease)

        if (project.tasks.hasProperty('publishApkRelease') || project.tasks.hasProperty('publishRelease')) {
            Assert.fail()
        }
    }

}
