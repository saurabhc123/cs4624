
import sbt._
import Keys._
import sbt.complete.Parsers.spaceDelimited

/**
  *  An SBT task to sync the project source code to a remote server.
  */
object Sync {
  lazy val sync = inputKey[Unit]("Sync the source code to a remote server")
  val task = sync := {
    val params = spaceDelimited("<arg>").parsed
    val property = System.getProperty("sync.remote")
    val remote = params.headOption.orElse(Option(property))
    remote match {
      case Some(r) => Seq("rsync", "-avz", "--delete", "--filter=:- .gitignore", "--exclude", ".git/", "./", r) !
      case None => println("Please specify the remote path as a parameter or set the 'sync.remote' system property. Example:\n\tuser@host:mypath/")
    }
  }
}
