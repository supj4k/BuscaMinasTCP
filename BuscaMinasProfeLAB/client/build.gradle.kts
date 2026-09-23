plugins {
    java
    application
}

repositories { mavenCentral() }

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
}

application {
    mainClass.set("org.buscaminasProfe.client.MainClient")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}