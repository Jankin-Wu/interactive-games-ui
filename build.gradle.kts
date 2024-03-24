import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}



group = "com.jankinwu"
version = "1.0-SNAPSHOT"


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.test {
    useJUnitPlatform()
}

//val coroutineVersion = "1.8.0"
val coroutineVersion = "1.6.0"
val ktorVersion = "2.3.9"
val coil3Version = "3.0.0-alpha06"
val skijaVersion = "0.93.6"

repositories {
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/jcenter")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://packages.jetbrains.team/maven/p/skija/maven/")
    google()
}

dependencies {
    testImplementation(kotlin("test"))
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
//    implementation(compose.material3)
//    implementation(compose.materialIconsExtended)
//    implementation(project(":AnimatedImage:library"))

    implementation("io.github.succlz123:compose-imageloader-desktop:0.0.2")
//    implementation("io.coil-kt.coil3:coil-core:$coil3Version")
//    implementation("io.coil-kt.coil3:coil-network-ktor:$coil3Version")
//    implementation("io.coil-kt.coil3:coil3:coil-gif:$coil3Version")
//    implementation("org.projectlombok:lombok:1.18.30")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.40")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
    implementation ("io.ktor:ktor-client-core:$ktorVersion")
    implementation ("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation ("io.ktor:ktor-client-cio:$ktorVersion")
//    implementation ("org.jetbrains.skiko:skiko-jvm:0.6.7")
    implementation("com.github.ltttttttttttt:load-the-image:1.0.8")
//    implementation ("org.jetbrains.skija:skija-shared:$skijaVersion")
//    implementation ("org.jetbrains.skija:skija-windows:$skijaVersion")
}

compose.desktop {
    application {
        mainClass = "MainKt"
        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "AoE2 Intergame UI"
            packageVersion = "1.0.0"
            windows {
                shortcut = true
                dirChooser = true
                menu = true
                upgradeUuid = "781660a5-c9c0-4e70-9d2a-1606d9fc9b12"
                iconFile.set(project.file("game.ico"))
                packageName = "AoE2 Intergame UI"
            }
        }
        jvmArgs("-Dskiko.renderApi=OPENGL")
    }
}
