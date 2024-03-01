plugins {
    kotlin("kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "cd.infoset.smaprtpay.merchant"
    compileSdk = 34

    ndkVersion = "26.2.11394342"

    defaultConfig {
        applicationId = "cd.infoset.smartpay.merchant"
        minSdk = 26
        targetSdk = 34
        versionCode = 1170
        versionName = "1.1.7"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            //isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "symbol_table"
            }
        }

        debug {
            matchingFallbacks.apply {
                clear()
                add("release")
            }
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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes.add("META-INF/*")
            excludes.add("LICENSE.txt")
            excludes.add("asm-license.txt")
        }
    }

    bundle {
        storeArchive {
            enable = false
        }
    }
}

val ktor_version: String by project
val room_version: String by project
val hilt_version: String by project
val lifecycle_version: String by project
val camera_version: String by project
val accompanist_version: String by project
val pay_works_version: String by project
val activity_version: String by project

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.activity:activity-compose:$activity_version")
    implementation("androidx.activity:activity-ktx:$activity_version")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    //Material design
    implementation("com.google.android.material:material:1.11.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Paging
    implementation("androidx.paging:paging-compose:3.2.1")

    // Barcode model dependencies
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // CameraX dependencies
    implementation("androidx.camera:camera-camera2:$camera_version")
    implementation("androidx.camera:camera-lifecycle:$camera_version")
    implementation("androidx.camera:camera-view:$camera_version")

    //Ktor and Serialization
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Compose APIs
    implementation(platform("androidx.compose:compose-bom:2024.02.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-livedata")

    //Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-compiler:$hilt_version")

    //Accompanist
    implementation("com.google.accompanist:accompanist-permissions:$accompanist_version")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanist_version")

    //SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    //CyberSource
    implementation("io.payworks:paybutton-android:$pay_works_version")
    implementation("io.payworks:mpos.android.taptophone:$pay_works_version")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))

    //test
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")
}

kapt {
    correctErrorTypes = true
}
