package contract.slack.testkit

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, _}
import com.wix.e2e.http.RequestHandler
import com.wix.e2e.http.client.sync._
import com.wix.e2e.http.server.WebServerFactory._

class FakeSlackServer(port: Int) {
  val server = aStubWebServer
    .onPort(port)
    .build

  private val handler: RequestHandler = {
    case HttpRequest(HttpMethods.POST, Path("/api/chat.postMessage"), _, entity, _) =>
      handlePostMessageToChannelRequest(entity)
  }

  def handlePostMessageToChannelRequest(entity: RequestEntity): HttpResponse = {
    val requestBody = entity.extractAs[PostMessageToChannelRequest]
    if (requestBody.channel.nonEmpty && requestBody.text.nonEmpty)
      aValidResponse()
    else
      aNotFoundResponse()
  }

  private def aValidResponse() =
    HttpResponse(
      status = StatusCodes.OK,
      entity = HttpEntity("""{ "ok": "true" }""")
    )

  private def aNotFoundResponse() =
    HttpResponse(status = StatusCodes.NotFound)

  server.appendAll(handler)
}

case class PostMessageToChannelRequest(channel: String, text: String)