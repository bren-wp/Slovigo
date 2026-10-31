plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

val buildNumber = (System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 0)
val keyFile = System.getenv("SLOVIGO_KEYSTORE_FILE")
val storePasswordEnv = System.getenv("SLOVIGO_STORE_PASSWORD")
val keyAliasEnv = System.getenv("SLOVIGO_KEY_ALIAS")
val keyPasswordEnv = System.getenv("SLOVIGO_KEY_PASSWORD")
val hasReleaseKey = !keyFile.isNullOrBlank() && !storePasswordEnv.isNullOrBlank() &&
    !keyAliasEnv.isNullOrBlank() && !keyPasswordEnv.isNullOrBlank()

android {
    namespace = "hr.brendigo.slovigo"
    compileSdk = 37

    defaultConfig {
        applicationId = "hr.brendigo.slovigo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1 + buildNumber
        versionName = if (buildNumber > 0) "0.1.$buildNumber" else "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        if (hasReleaseKey) {
            create("production") {
                storeFile = file(keyFile!!)
                storePassword = storePasswordEnv
                keyAlias = keyAliasEnv
                keyPassword = keyPasswordEnv
            }
        }
    }
    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            if (hasReleaseKey) signingConfig = signingConfigs.getByName("production")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = false
    }
    packaging {
        resources.excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.09.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.12.4")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
}
