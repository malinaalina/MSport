plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "m.alina.msport.core.mvi"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    flavorDimensions += "env"
    productFlavors {
        create("mock") {
            dimension = "env"
        }
        create("prod") {
            dimension = "env"
        }
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
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
}
