plugins {
    kotlin("jvm") version "1.4.0"
    id ("org.danilopianini.git-sensitive-semantic-versioning") version "0.2.2"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.12.0"
}

group = "io.github.corinz97"

repositories {
    jcenter()

}

val createClasspathManifest by tasks.registering {
    val outputDir = file("$buildDir/$name")
    inputs.files(sourceSets.main.get().runtimeClasspath)
    outputs.dir(outputDir)
    doLast {
        outputDir.mkdirs()
        file("$outputDir/plugin-classpath.txt").writeText(sourceSets.main.get().runtimeClasspath.joinToString("\n"))
    }
}

tasks.withType<Test> {
// The task type is defined in the Java plugin
    useJUnitPlatform() // Use JUnit 5 engine
    testLogging.showStandardStreams = true
    testLogging {
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
        events(*org.gradle.api.tasks.testing.logging.TestLogEvent.values())
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    testRuntimeOnly(files(createClasspathManifest))
    testImplementation(gradleTestKit()) // Test implementation: available for testing compile and runtime
    testImplementation("io.kotest:kotest-runner-junit5:4.2.5") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core:4.2.5") // for kotest core assertions
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.2.5") // for kotest core jvm assertions
}

gitSemVer {
    version = computeGitSemVer()
}

pluginBundle { // These settings are set for the whole plugin bundle
    website = "https://corinz97.github.io/"
    vcsUrl = "https://github.com/corinz97/cool-greetings"
    tags = listOf("example", "greetings", "corinz", "unibo")
}

gradlePlugin {
    plugins {
        create("GreetingsPlugin") { // One entry per plugin
            id = "${project.group}.${project.name}"
            displayName = "Corinz Greeting plugin"
            description = "Example plugin for the LSS course"
            implementationClass = "io.github.corinz97.GreetingPlugin"
        }
    }
}