plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)

}

android {
    namespace = "com.example.evchargingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.evchargingapp"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        viewBinding = true
        dataBinding = true
    }

}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.material3)
    implementation(libs.firebase.auth.ktx) // added this for the real-time database implementation
    implementation(libs.androidx.cardview)//this is for charging_stats
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database.ktx)
    // adding the retrofit dependencies to create a path between app and server so that app and server can talk
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //google map integration:
    // Google Maps SDK
    implementation(libs.play.services.maps)

    // Location services
    implementation(libs.play.services.maps)
    //noinspection UseTomlInstead
    implementation("com.google.android.gms:play-services-location:21.3.0")



    // Material Design + Navigation Drawer
    implementation(libs.androidx.drawerlayout)
    implementation(libs.material)

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")



}