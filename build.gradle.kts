import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.0.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.50"
}

group = "study"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating

configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

repositories {
    maven {
        url = uri("https://repo.spring.io/milestone")
    }

    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.projectreactor:reactor-kotlin-extensions:1.0.0.M2")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
//        exclude(module = "junit")
//        exclude(module = "mockito-core")
    }
    testImplementation("io.projectreactor:reactor-test")
//    testImplementation("org.junit.jupiter:junit-jupiter-api")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
//    testImplementation("com.ninja-squad:springmockk:1.1.2")
    testImplementation("org.hamcrest:hamcrest-library")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
