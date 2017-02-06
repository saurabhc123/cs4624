# Using SBT

SBT is Scala's build system. It will help you compile your code and download any
dependencies that you require.

If you are using an IDE like IntelliJ, all you have to do is import the SBT
project and IntelliJ will set everything up for you. For our project structure,
you can create an empty IntelliJ project, then import each sub-folder as its own
module. To do that, with your empty project open, go to "File > New Module With Existing Sources"
and then select the "build.sbt" file within the sub-folder. Then click "OK" on
the window that pops up. Once the SBT project is imported, you can run it by
right-clicking on the main class and choosing the "Run" option.

Alternatively, you can install the command-line tool for your platform
[here](http://www.scala-sbt.org/download.html). Once you have the command-line
tool, you can run `sbt run` to run the main class of your project. For more
commands/info, see SBT's [getting started guide](http://www.scala-sbt.org/0.13/docs/Getting-Started.html)
and [documentation](http://www.scala-sbt.org/0.13/docs/index.html).
