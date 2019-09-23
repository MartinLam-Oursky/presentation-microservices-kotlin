import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.1.7.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.2.71"
	kotlin("plugin.spring") version "1.2.71"
}

group = "com.oursky.presentation.microservices.kotlin"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
	jcenter()
}

val ktlint by configurations.creating

dependencies {
	ktlint("com.github.shyiko:ktlint:0.31.0")
	implementation("io.minio:minio:6.0.10")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.boot:spring-boot-starter-web:2.1.7.RELEASE")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.1.7.RELEASE")
	implementation("org.springframework.security:spring-security-core:5.1.6.RELEASE")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
	implementation("com.auth0:java-jwt:3.8.1")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.1.7.RELEASE")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}


tasks.register<JavaExec>("ktlint") {
	group = "verification"
	description = "Check Kotlin code style."
	classpath = ktlint
	main = "com.github.shyiko.ktlint.Main"
	args("--android", "src/**/*.kt")
}

tasks.named("check") {
	dependsOn(ktlint)
}

tasks.register<JavaExec>("ktlintFormat") {
	group = "formatting"
	description = "Fix Kotlin code style deviations."
	classpath = ktlint
	main = "com.github.shyiko.ktlint.Main"
	args("--android", "-F", "src/**/*.kt")
}