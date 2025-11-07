plugins {
    id("com.android.application")
}


android {
    namespace = "com.example.restaurantingredientsmanagement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.restaurantingredientsmanagement"
        minSdk = 26
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    implementation("androidx.room:room-runtime:2.7.1")
    annotationProcessor("androidx.room:room-compiler:2.7.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("io.github.ParkSangGwon:tedpermission-normal:3.4.2")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("androidx.activity:activity-ktx:1.2.3")
    implementation("androidx.fragment:fragment-ktx:1.3.3")
}