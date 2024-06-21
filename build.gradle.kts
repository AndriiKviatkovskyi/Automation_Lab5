plugins {
    id("java")
}


group = "org.kviat"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

// gradle :file-check -PfileName="build.gradle.kts"
// gradle :file-check -PfileName="SimpleTask.java"
// gradle :file-check -PfileName="DoesntExist.java"
// gradle :file-check -Pfile="build.gradle.kts"
tasks.register<Task>("file-check") {
    group = "Custom"

    doLast {
        val fileName = project.properties["fileName"] as String? ?: run {
            println("Please, use valid syntax:")
            println("gradle :file-check -PfileName=<filename>")
            return@doLast
        }
        val file = File(fileName)
        val files = fileTree(project.projectDir).files.filter { it.name == file.name }

        if (files.isEmpty()) {
            println("No file named $fileName was found")
        } else {
            println("There is/are ${files.size} file(s) with such name in this project" )
            files.forEach { println(it.relativeTo(project.projectDir)) }
        }
    }
}

// gradle :projectStructure
tasks.register("projectStructure", Task::class) {
    group = "Custom"

    doLast {
        val outputFile = File("$projectDir/project_structure.txt")
        val outputLines = mutableListOf<String>()

        fun listFiles(dir: File, indent: String = "") {
            dir.listFiles()?.forEach { file ->
                outputLines.add("$indent${file.name}")
                if (file.isDirectory) {
                    listFiles(file, "$indent  ")
                }
            }
        }

        listFiles(projectDir)
        outputFile.writeText(outputLines.joinToString("\n"))
        println("Project structure saved to: $outputFile")
    }
}