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
To create a sub-project, add a line `lazy val myProjectName = project` to the
build.sbt file. All project definitions go into the build.sbt file in the root
of the project. The name of the project variable will correspond to the project's
id (as well as its module name in IntelliJ). You should also add your project to
the call to aggregate on the root project. Also, configure the project's settings
similar to the other projects (`project.settings(Settings.commonSettings ++ Seq(otherSetting := "this value"))`.
For better code organization, you can use scala files under the project directory
(where the `Settings` and `Dependency` objects are defined).

If you change anything in the build definition, be sure to refresh the project in
IntelliJ (if you have "auto-import" turned on, it will automatically do this).

## Tips

### Working from a remote server over SSH

We are executing our code on a cluster that is typically accessed via SSH.
It is useful to have utilities that allow you to synchronize your local project
folder with one that is present on the remote server. This allows you to use
your existing development environment (IDEs, text editors, etc) on your local
computer and still quickly test your changes on the remote cluster. 

For this purpose, an SBT task (`sync`) was created to copy your project folder
to a remote server over SSH using the "rsync" command line utility. To use it,
you can run `sbt "sync <user@remote_host:my/project/path>"`. Alternatively, you
can set the system property "sync.remote" when running `sbt` instead of passing
the argument directly to the task: `sbt -Dsync.remote='user@remote_host:my/project/path`.
If you are running SBT commands often, it is faster to run an interactive prompt.
```
sbt -Dsync.remote='user@remote_host:my/project/path'
> sync                # will synchronize the project folder once.
> ~ sync              # will synchronize the project folder when any of the source
                      # files change. note that if you change the sbt project files
                      # at all, you need to exit this sync task, run the "reload"
                      # task to refresh the project, then you may restart the sync task.
```

This task will synchronize files from your local machine to the remote (ignoring any
paths from the gitignore). So you don't need to download the project to the remote first
(you can start with an empty folder and let this task copy everything up).

