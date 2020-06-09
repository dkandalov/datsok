import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
	java
	kotlin("jvm") version "1.3.72"
	`maven-publish`
}
group = "datsok"
version = "0.2"

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))
	testImplementation("junit:junit:4.12")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("datsok") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = URI("https://api.bintray.com/maven/dkandalov/maven/datsok/;publish=1")
            credentials {
                username = System.getenv("BINTRAY_USER")
                password = System.getenv("BINTRAY_API_KEY")
            }
        }
    }
}
