plugins {
    id("java")
}

group = "org.labs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.openjdk.jmh:jmh-core:1.36")

    implementation("commons-cli:commons-cli:1.5.0")
    implementation("org.slf4j:slf4j-api:2.0.10")
    implementation("ch.qos.logback:logback-classic:1.5.13")
}

tasks.test {
    useJUnitPlatform()
}