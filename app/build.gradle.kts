plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("jacoco")
}

android {
    namespace = "cl.duocuc.pagoflex"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.duocuc.pagoflex"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("pagoflex-release.jks")
            storePassword = "pagoflex123"
            keyAlias = "key0"
            keyPassword = "pagoflex123"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.0")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", 
        "**/*Test*.*", "android/**/*.*", "**/databinding/**/*.*", 
        "**/androidx/**/*.*", "**/*_Impl*.*"
    )
    val kotlinClasses = fileTree("${project.layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val javaClasses = fileTree("${project.layout.buildDirectory.get().asFile}/intermediates/javac/debug/classes") {
        exclude(fileFilter)
    }
    classDirectories.setFrom(files(kotlinClasses, javaClasses))

    val mainSrc = "${project.projectDir}/src/main/java"
    sourceDirectories.setFrom(files(mainSrc))

    executionData.setFrom(fileTree(project.layout.buildDirectory.get().asFile) {
        include(
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec", 
            "jacoco/testDebugUnitTest.exec", 
            "test-results/testDebugUnitTest/*.exec",
            "jacoco/*.exec"
        )
    })
}
