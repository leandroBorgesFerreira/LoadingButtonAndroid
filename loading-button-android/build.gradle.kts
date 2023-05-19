import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    signing
    `maven-publish`
}


val artifactIdVal = "loading-button-android"
val versionVal = "2.3.0"
val publicationName = "loadingButton"

group = "io.github.leandroborgesferreira"
version = versionVal

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            groupId = group.toString()
            artifactId = artifactIdVal
            version = versionVal

            pom {
                name.set("loading-button-android")
                description.set("A button that animates into a loading spinner")
                url.set("https://https://github.com/leandroBorgesFerreira/LoadingButtonAndroid")
                packaging = "jar"
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("leandroBorgesFerreira")
                        name.set("Leandro Borges Ferreira")
                        email.set("lehen01@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/leandroBorgesFerreira/LoadingButtonAndroid.git")
                    developerConnection.set("scm:git:ssh://github.com/leandroBorgesFerreira/LoadingButtonAndroid.git")
                    url.set("https://github.com/leandroBorgesFerreira/LoadingButtonAndroid")
                }
            }
        }
    }
}

signing {
    val secretPropsFile: File = project.rootProject.file("local.properties")

    if (secretPropsFile.exists()) {
        val prop = Properties().apply {
            load(FileInputStream(secretPropsFile))
        }
        useInMemoryPgpKeys(
            prop.getProperty("signing.keyId"),
            prop.getProperty("signing.key"),
            prop.getProperty("signing.password"),
        )
    } else {
        useInMemoryPgpKeys(
            System.getenv("SIGNING_KEY_ID"),
            System.getenv("SIGNING_KEY"),
            System.getenv("SIGNING_PASSWORD"),
        )
    }

    sign(publishing.publications[publicationName])
}

android {
    namespace = "io.github.leandroborgesferreira.loadingbutton"
    compileSdk = 33

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")

    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.robolectric:robolectric:4.10")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("junit:junit:4.13.2")
}
