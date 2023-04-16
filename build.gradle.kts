import java.nio.file.Path

plugins {
    kotlin("jvm") version "1.8.20"
    id("org.openapi.generator") version "6.5.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.helidon:helidon-dependencies:3.2.0"))
    implementation("io.helidon.config:helidon-config-yaml")
    implementation("io.helidon.health:helidon-health")
    implementation("io.helidon.health:helidon-health-checks")
    implementation("io.helidon.media:helidon-media-jsonb")
    implementation("io.helidon.metrics:helidon-metrics")
    implementation("io.helidon.openapi:helidon-openapi")
    implementation("io.helidon.webserver:helidon-webserver")
    implementation("io.helidon.webserver:helidon-webserver-static-content")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    testImplementation("io.helidon.webclient:helidon-webclient")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

val v1Spec: String = project.file("src/main/resources/v1.yaml").toString()
val openApiGeneratedSourceSet: Path = buildDir.toPath().resolve("generated/openapi")

sourceSets {
    val main by getting
    main.java.srcDir(openApiGeneratedSourceSet.resolve("src/main/java"))
}

openApiGenerate {
    generatorName.set("java-helidon-server")
    library.set("se")
    inputSpec.set(v1Spec)
    outputDir.set(openApiGeneratedSourceSet.toString())
    packageName.set("test")
    ignoreFileOverride.set(project.file(".openapi-generator-ignore").toString())
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "serializationLibrary" to "jsonb",
            "fullProject" to "false",
            "apiPackage" to "test.api",
            "modelPackage" to "test.models",
        )
    )
}

openApiValidate {
    inputSpec.set(v1Spec)
}

tasks.compileKotlin.configure {
    inputs.files(tasks.openApiGenerate.get().outputs)
}
