import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.kotlin_demo_wallet"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.kotlin_demo_wallet"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        android.buildFeatures.buildConfig = true

        val props = Properties().apply {
            val file = project.layout.projectDirectory.file("local.properties").asFile
            if (file.exists()) load(file.inputStream())
        }

        val apiBaseUrl = props.getProperty("API_BASE_URL") ?: ""
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        val authProxyBaseUrl = props.getProperty("AUTH_PROXY_BASE_URL") ?: ""
        buildConfigField("String", "AUTH_PROXY_BASE_URL", "\"$authProxyBaseUrl\"")
        val authProxyConfigId = props.getProperty("AUTH_PROXY_CONFIG_ID") ?: ""
        buildConfigField("String", "AUTH_PROXY_CONFIG_ID", "\"$authProxyConfigId\"")
        val organizationId = props.getProperty("ORGANIZATION_ID") ?: ""
        buildConfigField("String", "ORGANIZATION_ID", "\"$organizationId\"")
        val appScheme = props.getProperty("APP_SCHEME") ?: ""
        buildConfigField("String", "APP_SHEME", "\"$appScheme\"")
        val rpId = props.getProperty("RP_ID") ?: ""
        buildConfigField("String", "RP_ID", "\"$rpId\"")
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.okhttp)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    implementation("io.github.ethankonk:http:0.1.0-SNAPSHOT")
//    implementation("io.github.ethankonk:types:0.1.0-SNAPSHOT")
//    implementation("io.github.ethankonk:stamper:0.1.0-SNAPSHOT")
//    implementation("io.github.ethankonk:sdk-kotlin:0.1.0-SNAPSHOT")
//    implementation("io.github.ethankonk:passkey:0.1.0-SNAPSHOT")

//    implementation("com.turnkey:http:0.1.0-beta.1")
//    implementation("com.turnkey:types:0.1.0-beta.1")
//    implementation("com.turnkey:stamper:0.1.0-beta.1")
//    implementation("com.turnkey:sdk-kotlin:0.1.0-beta.1")
//    implementation("com.turnkey:passkey:0.1.0-beta.1")

    implementation(project(":packages:http"))
    implementation(project(":packages:types"))
    implementation(project(":packages:stamper"))
    implementation(project(":packages:sdk-kotlin"))
    implementation(project(":packages:passkey"))
}