
// Overrides a bunch of tasks to default to UTF-8, because for whatever reason,
// that is _still_ not the default encoding for all these tasks. :(

tasks.withType<AntlrTask>().configureEach {
    arguments = arguments + listOf("-encoding", "UTF-8")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Test>().configureEach {
    defaultCharacterEncoding = "UTF-8"
}

tasks.withType<JavaExec>().configureEach {
    defaultCharacterEncoding = "UTF-8"
}
