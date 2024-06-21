plugins {
    id("java")
}

group = "org.kviat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(mapOf("path" to ":core")))
    implementation(project(mapOf("path" to ":data")))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("org.jdatepicker:jdatepicker:1.3.4")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}