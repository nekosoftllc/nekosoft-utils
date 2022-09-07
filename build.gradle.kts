import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("maven-publish")
    id("signing")
    id("me.qoomon.git-versioning") version "6.3.0"
    id("org.jetbrains.dokka") version "1.7.10"
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.7.10"
}

group = "org.nekosoft.utils"
version = "0.0.0-SNAPSHOT"

version = "0.0.0-SNAPSHOT"
gitVersioning.apply {
    // https://github.com/qoomon/gradle-git-versioning-plugin
    refs {
        branch(".+") {
            version = "\${ref}-SNAPSHOT"
        }
        tag("v(?<version>.*)") {
            version = "\${ref.version}"
        }
    }
    // optional fallback configuration in case of no matching ref configuration
    rev {
        version = "\${commit}"
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework:spring-web")
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.apache.tomcat.embed:tomcat-embed-core")
    implementation("org.aspectj:aspectjweaver")
    implementation("jakarta.persistence:jakarta.persistence-api")

    testImplementation("io.mockk:mockk:1.12.7")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

//tasks.withType<DokkaTask> {
//    inputs.dir("src/main/kotlin")
//}

//tasks {
//    val dokkaJavadoc by getting(DokkaTask::class)
//    "javadocJar" {
//        dependsOn(dokkaJavadoc)
//    }
//}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = if (project.hasProperty("ossrhUsername"))
                    project.properties.get("ossrhUsername").toString()
                else
                    System.getenv("OSSRH_USERNAME")
                password = if (project.hasProperty("ossrhPassword"))
                    project.properties.get("ossrhPassword").toString()
                else
                    System.getenv("OSSRH_PASSWORD")
            }
        }
    }
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = "utils-common"
                from(components["java"])
                pom {
                    name.set("NekoSoft Utils Common")
                    description.set("A set of utility functions and classes in various areas")
                    url.set("https://git.nekosoft.org/nekosoft/nekosoft-utils")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("fedmest")
                            name.set("Federico Mestrone")
                            email.set("federico@nekosoft.org")
                            organization.set("NekoSoft LLC")
                            organizationUrl.set("https://www.nekosoft.org")
                        }
                    }
                    scm {
                        connection.set("scm:git:git@git.nekosoft.org:nekosoft/nekosoft-utils.git")
                        developerConnection.set("scm:git:git@git.nekosoft.org:nekosoft/nekosoft-utils.git")
                        url.set("https://git.nekosoft.org/nekosoft/nekosoft-utils")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}