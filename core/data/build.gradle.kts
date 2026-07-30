plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
}

android {
    namespace = "m.alina.msport.core.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        buildConfigField("String", "SERVER_BASE_URL", "\"http://10.0.2.2:8080/\"")
    }

    flavorDimensions += "env"
    productFlavors {
        create("mock") {
            dimension = "env"
            buildConfigField("boolean", "USE_MOCK_DATA", "true")
        }
        create("prod") {
            dimension = "env"
            buildConfigField("boolean", "USE_MOCK_DATA", "false")
        }
    }

    buildFeatures {
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
    implementation(project(":core:model"))
    api(project(":core:network"))
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(libs.androidx.core.ktx)

    // Dagger 2
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    // Retrofit & OkHttp
    api(libs.retrofit)
    implementation(libs.retrofit.gson)
    api(libs.okhttp)

    implementation(libs.kotlinx.coroutines.core)
}
