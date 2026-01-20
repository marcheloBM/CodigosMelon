plugins {
    id("com.android.application")

}

android {
    namespace = "com.Burgos.codigosmelon"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.Burgos.codigosmelon"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "4.1"
    }

    buildFeatures {
        viewBinding = true
    }
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.sqlite:sqlite:2.3.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
    testImplementation(kotlin("test"))
}