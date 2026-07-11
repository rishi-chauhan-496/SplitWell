import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    rootProject.file("app/appconfig.properties").inputStream().use { inputStream ->
        load(inputStream)
    }
}

android {
    signingConfigs {
        val appconfigFile = rootProject.file("app/appconfig.properties")

        getByName("debug") {
            if (appconfigFile.exists()) {
                val appconfig = Properties().apply {
                    load(appconfigFile.inputStream())
                }
                storeFile = file(appconfig["DEBUG_FILE_PATH"] as String)
                storePassword = appconfig["DEBUG_PASSWORD"] as String
                keyAlias = appconfig["DEBUG_ALIAS"] as String
                keyPassword = appconfig["DEBUG_PASSWORD"] as String
            }
            // If appconfig.properties doesn't exist (CI environment),
            // Android falls back to its own auto-generated debug keystore.
        }

        create("release") {
            if (appconfigFile.exists()) {
                val appconfig = Properties().apply {
                    load(appconfigFile.inputStream())
                }
                storeFile = file(appconfig["RELEASE_FILE_PATH"] as String)
                storePassword = appconfig["RELEASE_PASSWORD"] as String
                keyAlias = appconfig["RELEASE_ALIAS"] as String
                keyPassword = appconfig["RELEASE_PASSWORD"] as String
            }
        }
    }
    namespace = "com.app.splitwell"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.app.splitwell"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.foundation.layout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    //navigation
    implementation("androidx.navigation:navigation-compose:2.9.7")
    //coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    // Koin for Kotlin apps
    implementation("io.insert-koin:koin-core:3.5.6")
    implementation("io.insert-koin:koin-android:3.5.6")
    implementation("io.insert-koin:koin-androidx-compose:3.5.6")
    //google login
    implementation(platform("com.google.firebase:firebase-bom:34.11.0"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    // Also add the dependencies for the Credential Manager libraries and specify their versions
    implementation("androidx.credentials:credentials:1.6.0-rc02")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0-rc02")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.2.0")
    //test
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    //network log
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.4.0")
}