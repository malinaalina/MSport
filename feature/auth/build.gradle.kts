plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kapt)
}

android {
    namespace = "m.alina.msport.feature.auth"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    flavorDimensions += "env"
    productFlavors {
        create("mock") {
            dimension = "env"
            buildConfigField("int", "SMS_CODE_LENGTH", "4")
        }
        create("prod") {
            dimension = "env"
            buildConfigField("int", "SMS_CODE_LENGTH", "4")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:mvi"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
