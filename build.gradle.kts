plugins {
    java
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "pl.msz.apc"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone")
}

extra["springAiVersion"] = "1.0.0-M1"
extra["springModulithVersion"] = "1.2.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")
    implementation("org.springframework.ai:spring-ai-pgvector-store-spring-boot-starter")
    implementation("org.springframework.ai:spring-ai-pdf-document-reader")
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("me.paulschwarz:spring-dotenv:4.0.0")
    implementation("com.google.genai:google-genai:0.4.0")
    
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.postgresql:postgresql") // For pgvector types if needed
    
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

springBoot {
    mainClass.set("pl.msz.apc.ApcApplication")
}
