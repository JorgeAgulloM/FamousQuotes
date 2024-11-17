import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.androidHilt)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
}

val keystorePropertiesFile: File = file("../signing/signing.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

val adMobPropertiesFile: File = file("../admob/admob.properties")
val adMobProperties = Properties()
adMobProperties.load(FileInputStream(adMobPropertiesFile))

android {
    namespace = "com.softyorch.famousquotes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.softyorch.famousquotes"
        minSdk = 26
        targetSdk = 35
        versionCode = 133
        versionName = "1.3.3"

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

    applicationVariants.all {

        val variant = this
        val flavor = this.productFlavors[0].name
        val buildType = variant.buildType.name

        val appLabelMap = when (this.buildType.name) {
            "release" -> mapOf(
                "historical" to adMobProperties["HISTORICAL_RELEASE_KEY_ID_ADMOB_APP"],
                "uplifting" to adMobProperties["UPLIFTING_RELEASE_KEY_ID_ADMOB_APP"],
                "biblical" to adMobProperties["BIBLICAL_RELEASE_KEY_ID_ADMOB_APP"]
            )

            else -> mapOf(
                "historical" to adMobProperties["FAKE_RELEASE_KEY_ID_ADMOB_APP"],
                "uplifting" to adMobProperties["FAKE_RELEASE_KEY_ID_ADMOB_APP"],
                "biblical" to adMobProperties["FAKE_RELEASE_KEY_ID_ADMOB_APP"]
            )
        }

        variant.outputs.all {
            variant.mergedFlavor.manifestPlaceholders["ID_ADMOB_APP"] = "${appLabelMap[flavor]}"

            if (buildType == "release") {
                println("Active flavor in release buildType: $flavor")
                buildConfigField("String", "PROVIDER_AUTHORITIES", "\"com.softyorch.famousquotes.$flavor.fileprovider\"")
                variant.mergedFlavor.manifestPlaceholders["PROVIDER_AUTHORITIES"] = "com.softyorch.famousquotes.$flavor.fileprovider"

                when (flavor) {
                    "historical" -> {
                        buildConfigField("String", "ID_BANNER_HOME", adMobProperties["HISTORICAL_RELEASE_KEY_ID_BANNER_HOME"].toString())
                        buildConfigField("String", "ID_INTERSTITIAL_OTHER_QUOTE", adMobProperties["HISTORICAL_RELEASE_KEY_ID_INTERSTITIAL_OTHER_QUOTE"].toString())
                        buildConfigField("String", "ID_BONIFIED_DOWNLOAD_IMAGE", adMobProperties["HISTORICAL_RELEASE_KEY_ID_BONIFIED_DOWNLOAD_IMAGE"].toString())
                        variant.mergedFlavor.manifestPlaceholders["ICON_NOTIFICATION_FLAVOUR"] = "@drawable/historical_icon"
                    }
                    "uplifting" -> {
                        buildConfigField("String", "ID_BANNER_HOME", adMobProperties["UPLIFTING_RELEASE_KEY_ID_BANNER_HOME"].toString())
                        buildConfigField("String", "ID_INTERSTITIAL_OTHER_QUOTE", adMobProperties["UPLIFTING_RELEASE_KEY_ID_INTERSTITIAL_OTHER_QUOTE"].toString())
                        buildConfigField("String", "ID_BONIFIED_DOWNLOAD_IMAGE", adMobProperties["UPLIFTING_RELEASE_KEY_ID_BONIFIED_DOWNLOAD_IMAGE"].toString())
                        variant.mergedFlavor.manifestPlaceholders["ICON_NOTIFICATION_FLAVOUR"] = "@drawable/uplifting_icon"
                    }
                    "biblical" -> {
                        buildConfigField("String", "ID_BANNER_HOME", adMobProperties["BIBLICAL_RELEASE_KEY_ID_BANNER_HOME"].toString())
                        buildConfigField("String", "ID_INTERSTITIAL_OTHER_QUOTE", adMobProperties["BIBLICAL_RELEASE_KEY_ID_INTERSTITIAL_OTHER_QUOTE"].toString())
                        buildConfigField("String", "ID_BONIFIED_DOWNLOAD_IMAGE", adMobProperties["BIBLICAL_RELEASE_KEY_ID_BONIFIED_DOWNLOAD_IMAGE"].toString())
                        variant.mergedFlavor.manifestPlaceholders["ICON_NOTIFICATION_FLAVOUR"] = "@drawable/biblical_icon"
                    }
                }
            } else {
                buildConfigField("String", "ID_BANNER_HOME", adMobProperties["FAKE_RELEASE_KEY_ID_BANNER_HOME"].toString())
                buildConfigField("String", "ID_INTERSTITIAL_OTHER_QUOTE", adMobProperties["FAKE_RELEASE_KEY_ID_INTERSTITIAL_OTHER_QUOTE"].toString())
                buildConfigField("String", "ID_BONIFIED_DOWNLOAD_IMAGE", adMobProperties["FAKE_RELEASE_KEY_ID_BONIFIED_DOWNLOAD_IMAGE"].toString())
                buildConfigField("String", "PROVIDER_AUTHORITIES", "\"com.softyorch.famousquotes.$flavor.dev.fileprovider\"")
                variant.mergedFlavor.manifestPlaceholders["ICON_NOTIFICATION_FLAVOUR"] = "@drawable/default_icon"
                variant.mergedFlavor.manifestPlaceholders["PROVIDER_AUTHORITIES"] = "com.softyorch.famousquotes.$flavor.dev.fileprovider"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".dev"
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
            buildConfigField("String", "ICON", "\"historical_icon\"")
            buildConfigField("int", "PRIMARY_COLOR", "0xFFFFB005")
            buildConfigField("int", "SECONDARY_COLOR", "0xFFFFD966")
            buildConfigField("int", "BACKGROUND_COLOR", "0xFF00253A")
            android.buildFeatures.buildConfig = true
        }

        create("uplifting") {
            dimension = "version"
            applicationIdSuffix = ".uplifting"
            buildConfigField("String", "DB_VERSION", "\"uplifting_db_update_version\"")
            buildConfigField("String", "APP_TITLE", "\"app_name_uplifting\"")
            buildConfigField("String", "DB_COLLECTION", "\"uplifting_quotes\"")
            buildConfigField("String", "ICON", "\"uplifting_icon\"")
            buildConfigField("int", "PRIMARY_COLOR", "0xFF99CE00")
            buildConfigField("int", "SECONDARY_COLOR", "0xFFBAD56B")
            buildConfigField("int", "BACKGROUND_COLOR", "0xFF00253A")
            android.buildFeatures.buildConfig = true
        }

        create("biblical") {
            dimension = "version"
            applicationIdSuffix = ".biblical"
            buildConfigField("String", "DB_VERSION", "\"biblical_db_update_version\"")
            buildConfigField("String", "APP_TITLE", "\"app_name_biblical\"")
            buildConfigField("String", "DB_COLLECTION", "\"biblical_quotes\"")
            buildConfigField("String", "ICON", "\"biblical_icon\"")
            buildConfigField("int", "PRIMARY_COLOR", "0xFF3182BD")
            buildConfigField("int", "SECONDARY_COLOR", "0xFF3182BD")
            buildConfigField("int", "BACKGROUND_COLOR", "0xFF7FA0BB")
            android.buildFeatures.buildConfig = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE-notice.md"
            excludes += "/META-INF/LICENSE.md"
        }
    }
}

dependencies {

    // Splash
    implementation(libs.androidx.core.splashscreen)

    // App-update
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

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
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Material
    implementation(libs.material)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.mockk.android)
}
