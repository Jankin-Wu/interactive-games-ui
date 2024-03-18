import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}



group = "com.jankinwu"



version = "1.0-SNAPSHOT"
val coroutineVersion = "1.8.0"
val ktorVersion = "2.3.9"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/jcenter")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("io.github.succlz123:compose-imageloader-desktop:0.0.2")
    implementation("javax.websocket:javax.websocket-api:1.1")
    implementation("org.apache.tomcat.embed:tomcat-embed-websocket:9.0.52")
    implementation("org.projectlombok:lombok:1.18.30")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.40")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
    implementation ("io.ktor:ktor-client-core:$ktorVersion")
    implementation ("io.ktor:ktor-client-websockets:$ktorVersion")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "intergame-ui"
            packageVersion = "1.0.0"
        }
        jvmArgs("-Dskiko.renderApi=OPENGL")
    }
}
