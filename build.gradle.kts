plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}



//buildscript {
//    repositories {
//        google()
//        mavenCentral()
//    }
// dependencies {
//     classpath("com.android.tools.build:gradle:8.2.2")
//     classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
// }
//}
//
//
//allprojects {
//    repositories {
//        google()
//        mavenCentral()
//    }
//}
//
//task clean(type: Delete) {
//    delete rootProject.buildDir
//}



