package com.mounacheikhna.publish

import com.android.build.gradle.AppPlugin
import org.apache.commons.lang.StringUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class PublishPlugin implements Plugin<Project> {

  public static final String PLAY_GROUP = "Play Store"

  private Project project
  private PublishExtension extension

  @Override
  void apply(Project project) {
    def log = project.logger

    this.project = project
    def hasAppPlugin = project.plugins.find { p -> p instanceof AppPlugin }
    if (!hasAppPlugin) {
      throw new IllegalStateException("The 'com.android.application' plugin is required.")
    }
    extension = project.extensions.create('play', PublishExtension)

    project.android.applicationVariants.all { variant ->
      if (variant.buildType.isDebuggable()) {
        log.debug("Skipping debuggable build type ${variant.buildType.name}.")
        return
      }

      def buildTypeName = variant.buildType.name.capitalize()

      def productFlavorNames = variant.productFlavors.collect { it.name.capitalize() }
      if (productFlavorNames.isEmpty()) {
        productFlavorNames = [""]
      }
      def productFlavorName = productFlavorNames.join('')
      def flavor = StringUtils.uncapitalize(productFlavorName)

      def variationName = "${productFlavorName}${buildTypeName}"

      def outputData = variant.outputs.first()
      def zipAlignTask = outputData.zipAlign
      def assembleTask = outputData.assemble
      def variantData = variant.variantData

      def organizeScreenshotsTask = createOrganizeScreenshotsTask(variationName)

      def bootstrapTask = createBootstrapTask(variationName, variant, flavor)
      def playResourcesTask = createPlayResourcesTask(flavor, variant,
              variationName)

      def publishListingTask = createPublishListingTask(variationName, variant, playResourcesTask)

      if (zipAlignTask && variantData.zipAlignEnabled) {
        def publishApkTask = createPublishApkTask(variant, playResourcesTask, variationName)

        def publishTask = createPublishTask(variationName, publishApkTask, publishListingTask)
        publishApkTask.dependsOn playResourcesTask
        publishApkTask.dependsOn assembleTask
      } else {
        log.warn(
                "Could not find ZipAlign task. Did you specify a signingConfig for the variation ${variationName}?")
      }
    }
  }

  private createOrganizeScreenshotsTask(variationName) {
    def organizeScreenshotsTaskName = "organizeScreenshots${variationName}"
    def organizeScreenshotsTask = project.tasks.create(organizeScreenshotsTaskName)
    organizeScreenshotsTask.description = "Organize screenshots images using naming convention for " +
            "each play folder for the ${variationName} build"
    organizeScreenshotsTask.group = PLAY_GROUP
    organizeScreenshotsTask
  }

  private Task createPublishTask(variationName,
          PublishApkTask publishApkTask, PublishListingTask publishListingTask) {
    def publishTaskName = "publish${variationName}"
    def publishTask = project.tasks.create(publishTaskName)
    publishTask.description = "Updates APK and play store listing for the ${variationName} build"
    publishTask.group = PLAY_GROUP

    // Attach tasks to task graph.
    publishTask.dependsOn publishApkTask
    publishTask.dependsOn publishListingTask
  }

  private PublishApkTask createPublishApkTask(variant,
          createPlayFilesTask playResourcesTask, variationName) {
    def publishApkTaskName = "publishApk${variationName}"
    def publishApkTask = project.tasks.create(publishApkTaskName, PublishApkTask)
    publishApkTask.extension = extension
    publishApkTask.variant = variant
    publishApkTask.inputFolder = playResourcesTask.outputFolder
    publishApkTask.description = "Uploads the APK for the ${variationName} build"
    publishApkTask.group = PLAY_GROUP
    publishApkTask
  }

  private PublishListingTask createPublishListingTask(variationName, variant,
          createPlayFilesTask playResourcesTask) {
    def publishListingTaskName = "publishListing${variationName}"
    def publishListingTask = project.tasks.create(publishListingTaskName, PublishListingTask)
    publishListingTask.extension = extension
    publishListingTask.variant = variant
    publishListingTask.inputFolder = playResourcesTask.outputFolder
    publishListingTask.description =
            "Updates the play store listing for the ${variationName} build"
    publishListingTask.group = PLAY_GROUP

    // Attach tasks to task graph.
    publishListingTask.dependsOn playResourcesTask
    publishListingTask
  }

   createPlayFilesTask createPlayResourcesTask(flavor,
          variant, variationName) {
    def playResourcesTaskName = "generate${variationName}PlayResources"
    def playResourcesTask = project.tasks.create(playResourcesTaskName,
            createPlayFilesTask)
    playResourcesTask.inputs.file(new File(project.projectDir, "src/main/play"))
    if (StringUtils.isNotEmpty(flavor)) {
      playResourcesTask.inputs.file(new File(project.projectDir, "src/${flavor}/play"))
    }
    playResourcesTask.inputs.file(
            new File(project.projectDir, "src/${variant.buildType.name}/play"))
    if (StringUtils.isNotEmpty(flavor)) {
      playResourcesTask.inputs.file(new File(project.projectDir, "src/${variant.name}/play"))
    }

    playResourcesTask.outputFolder =
            new File(project.projectDir, "build/outputs/play/${variant.name}")
    playResourcesTask.description =
            "Collects play store resources for the ${variationName} build"
    playResourcesTask.group = PLAY_GROUP
    playResourcesTask
  }

  private Task createBootstrapTask(variationName, variant, flavor) {
    def bootstrapTaskName = "bootstrap${variationName}PlayResources"
    def bootstrapTask = project.tasks.create(bootstrapTaskName, InitTask)
    bootstrapTask.extension = extension
    bootstrapTask.variant = variant
    if (StringUtils.isNotEmpty(flavor)) {
      bootstrapTask.outputFolder = new File(project.projectDir, "src/${flavor}/play")
    } else {
      bootstrapTask.outputFolder = new File(project.projectDir, "src/main/play")
    }
    bootstrapTask.description =
            "Downloads the play store listing for the ${variationName} build. No download of image resources. See #18."
    bootstrapTask.group = PLAY_GROUP
    bootstrapTask
  }
}