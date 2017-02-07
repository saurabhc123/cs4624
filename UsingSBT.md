# Using SBT

SBT is Scala's build system. It will help you compile your code and download any
dependencies that you require.

If you are using an IDE like IntelliJ, all you have to do is import the SBT
project and IntelliJ will set everything up for you. To do that, click 
"Import Project" on the startup window, choose "build.sbt" file in the root
directory of this project. Press "OK" on the window that pops up.

Alternatively, you can install the command-line tool for your platform
[here](http://www.scala-sbt.org/download.html). Once you have the command-line
tool, you can run `sbt run` to run the main class of your project. For more
commands/info, see SBT's [getting started guide](http://www.scala-sbt.org/0.13/docs/Getting-Started.html)
and [documentation](http://www.scala-sbt.org/0.13/docs/index.html).

## Adding sub-projects
To create a sub-project, make a "build.sbt" file that's structured like this:

```scala
lazy val myProjectName = (project in file(".")).
  settings(
    name := "MyProject",
    scalaVersion := "2.10.4"
  )
```

The name of the variable corresponds to the project id and the module name 
within IntelliJ, so make sure it is not the same as any of the other 
sub-projects. In the project's root "build.sbt" file, add 
`lazy val myProjectName = project` to the top. In the
call to `.aggregate`, add `myProjectName` as one of the arguments. Refresh the
project in IntelliJ (if you don't have auto-import turned on) and it will
automatically adjust the modules of the project.