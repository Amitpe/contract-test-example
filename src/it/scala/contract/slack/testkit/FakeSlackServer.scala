package contract.slack.testkit

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, _}
import com.wix.e2e.http.RequestHandler
import com.wix.e2e.http.client.sync._
import com.wix.e2e.http.server.WebServerFactory._
import contract.json.JsonJacksonMarshaller
import contract.slack.sync.{PostMessageToChannelRequest, PostMessageToChannelResponse}

class FakeSlackServer(port: Int,
                      token: String,
                      channels: Seq[String]) {
  type ErrorCode = String
  implicit private val mapper = new JsonJacksonMarshaller

  val server = aStubWebServer
    .onPort(port)
    .build

  private val handler: RequestHandler = {
    case HttpRequest(_, _, headers, _, _)
      if !headers.contains(RawHeader("authorization", s"Bearer $token")) =>
      handleInvalidTokenRequest()
    case HttpRequest(HttpMethods.POST, Path("/api/chat.postMessage"), _, entity, _) =>
      handlePostMessageToChannelRequest(entity)
    case _ =>
      handleUnknownRequest()
  }

  def handleInvalidTokenRequest() =
    anErrorResponseWith("not_authed")

  def handlePostMessageToChannelRequest(entity: RequestEntity): HttpResponse =
    validateRequestBody(entity) match {
      case Some(errorCode) => anErrorResponseWith(errorCode)
      case _ => aValidResponse()
    }

  private def validateRequestBody(entity: RequestEntity): Option[ErrorCode] = {
    val requestBody = entity.extractAs[PostMessageToChannelRequest]
    if (!channels.contains(requestBody.channel))
      Some("channel_not_found")
    else
      None
  }

  private def handleUnknownRequest() =
    aNotFoundResponse

  private def anErrorResponseWith(error: String) =
    HttpResponse(
      status = StatusCodes.OK,
      entity = HttpEntity(mapper.marshall(PostMessageToChannelResponse(ok = false, error = error)))
    )

  private def aValidResponse() =
    HttpResponse(
      status = StatusCodes.OK,
      entity = HttpEntity(mapper.marshall(PostMessageToChannelResponse(ok = true, error = null)))
    )

  private def aNotFoundResponse: HttpResponse =
    HttpResponse(
      status = StatusCodes.NotFound,
      entity = HttpEntity(s"Request's path is not supported in slack Test-kit.")
    )

  server.appendAll(handler)
}