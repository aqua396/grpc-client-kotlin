import com.google.protobuf.gradle.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("com.google.protobuf")
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.2")
    defaultConfig {
        applicationId = "tutorial.grpc.client.kotlin"
        minSdkVersion(16)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    val kotlinVersion by extra("1.3.50")
    val grpcVersion by extra("1.3.0")
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

    // gRPC
    implementation("io.grpc:grpc-okhttp:$grpcVersion")
    implementation("io.grpc:grpc-protobuf-lite:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.android.material:material:1.1.0")
}

protobuf {
    val grpcVersion by extra("1.3.0")
    protoc {
        artifact = "com.google.protobuf:protoc:3.1.0"
    }
    plugins {
        id("javalite") {
            artifact = "com.google.protobuf:protoc-gen-javalite:3.0.0"
        }
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                //  remove("java")
            }

            it.plugins{
                create("javalite"){ }
                create("grpc") {
                    option("lite")
                }
            }
        }
    }
}
