package cs4624.common

import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSClient

object Http {
    implicit val client: WSClient = NingWSClient()
}