plugins {
	java
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.amarildo"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("se.michaelthelin.spotify:spotify-web-api-java:9.3.0")

	// reduce boilerplate code
	compileOnly("org.projectlombok:lombok")
	compileOnly("org.mapstruct:mapstruct:1.6.3")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
	annotationProcessor("org.projectlombok:lombok")
	implementation("org.jetbrains:annotations:26.0.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
