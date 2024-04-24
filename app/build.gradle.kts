import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    alias(libs.plugins.androidHilt)
    alias(libs.plugins.googleServices)
}

val keystorePropertiesFile: File = file("../signing/signing.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

val adMobPropertiesFile: File = file("../admob/admob.properties")
val adMobProperties = Properties()
adMobProperties.load(FileInputStream(adMobPropertiesFile))

android {
    namespace = "com.softyorch.famousquotes"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.softyorch.famousquotes"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["RELEASE_KEY_ALIAS"] as String
            keyPassword = keystoreProperties["RELEASE_KEY_PASSWORD"] as String
            storeFile = file(keystoreProperties["RELEASE_KEYSTORE_PATH"] as String)
            storePassword = keystoreProperties["RELEASE_KEYSTORE_PASSWORD"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            manifestPlaceholders["ID_ADMOB_APP"] = adMobProperties["RELEASE_KEY_ID_ADMOB_APP"] ?: ""
            buildConfigField("String", "ID_BANNER_HOME", adMobProperties["RELEASE_KEY_ID_BANNER_HOME"].toString())
            buildConfigField("String", "ID_INTERSTITIAL_OTHER_QUOTE", adMobProperties["RELEASE_KEY_ID_INTERSTITIAL_OTHER_QUOTE"].toString())
        }
        debug {
            applicationIdSuffix = ".dev"
            manifestPlaceholders["ID_ADMOB_APP"] = adMobProperties["FAKE_RELEASE_KEY_ID_ADMOB_APP"] ?: ""
            buildConfigField("String", "ID_BANNER_HOME", adMobProperties["FAKE_RELEASE_KEY_ID_BANNER_HOME"].toString())
            buildConfigField("String", "ID_INTERSTITIAL_OTHER_QUOTE", adMobProperties["FAKE_RELEASE_KEY_ID_INTERSTITIAL_OTHER_QUOTE"].toString())
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("historical") {
            applicationIdSuffix = ".historical"
            dimension = "version"
            buildConfigField("String", "DB_VERSION", "\"historical_db_update_version\"")
            buildConfigField("String", "APP_TITLE", "\"app_name_historical\"")
            buildConfigField("String", "DB_COLLECTION", "\"historical_quotes\"")
            buildConfigField("int", "PRIMARY_COLOR", "0xFFFFB005")
            buildConfigField("int", "SECONDARY_COLOR", "0xFFFFD966")
            buildConfigField("int", "BACKGROUND_COLOR", "0xFF8ECAE6")
            android.buildFeatures.buildConfig = true
        }

        create("uplifting") {
            dimension = "version"
            applicationIdSuffix = ".uplifting"
            buildConfigField("String", "DB_VERSION", "\"uplifting_db_update_version\"")
            buildConfigField("String", "APP_TITLE", "\"app_name_uplifting\"")
            buildConfigField("String", "DB_COLLECTION", "\"uplifting_quotes\"")
            buildConfigField("int", "PRIMARY_COLOR", "0xFF99CE00")
            buildConfigField("int", "SECONDARY_COLOR", "0xFFBAD56B")
            buildConfigField("int", "BACKGROUND_COLOR", "0xFF88ABC2")
            android.buildFeatures.buildConfig = true
        }

        create("biblical") {
            dimension = "version"
            applicationIdSuffix = ".biblical"
            buildConfigField("String", "DB_VERSION", "\"biblical_db_update_version\"")
            buildConfigField("String", "APP_TITLE", "\"app_name_biblical\"")
            buildConfigField("String", "DB_COLLECTION", "\"biblical_quotes\"")
            buildConfigField("int", "PRIMARY_COLOR", "0xFF3182BD")
            buildConfigField("int", "SECONDARY_COLOR", "0xFF6BAED6")
            buildConfigField("int", "BACKGROUND_COLOR", "0xFFC6DBEF")
            android.buildFeatures.buildConfig = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Material Icons
    implementation(libs.androidx.material.icons.extended)

    //Datastore
    implementation(libs.androidx.datastore.preferences)

    // GoogleFonts
    implementation(libs.androidx.ui.text.google.fonts)

    // Coil
    implementation(libs.coil.compose)

    //Google Admob
    implementation(libs.play.services.ads)
    implementation(libs.user.messaging.platform)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-config-ktx")

    // Dagger Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}