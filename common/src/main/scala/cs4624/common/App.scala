package cs4624.common

trait App extends scala.App {
    override def main(args: Array[String]) = {
        try {
            super.main(args)
        } finally {
            /**
             * Cleanup code.
             */
            Http.client.close()
        }
    }
}