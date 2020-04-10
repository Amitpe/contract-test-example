package contract.provider.testkit

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, _}
import com.wix.e2e.http.client.sync._
import com.wix.e2e.http.server.WebServerFactory._
import com.wix.e2e.http.{HttpRequest, RequestHandler}
import contract.json.JsonJacksonMarshaller
import contract.provider.sync.{PostMessageToChannelRequest, PostMessageToChannelResponse}

class FakeSlackServer(port: Int,
                      token: String,
                      channels: Seq[String]) {
  type ErrorCode = String
  implicit private val mapper = new JsonJacksonMarshaller

  private val handler: RequestHandler = {
    case HttpRequest(_, _, headers, _ ,_) if !headers.contains(RawHeader("authorization", s"Bearer $token")) =>
      okResponseWith(PostMessageToChannelResponse(ok = false, error = "not_authed"))
    case httpRequest@HttpRequest(HttpMethods.POST, Path("/api/chat.postMessage"), _, _, _) =>
      handlePostMessageToChannelRequest(httpRequest)
    case _ =>
      responseNotFound
  }

  def handlePostMessageToChannelRequest(httpRequest: HttpRequest): HttpResponse =
    validateRequestBody(httpRequest) match {
      case Some(errorCode) =>
        okResponseWith(PostMessageToChannelResponse(ok = false, error = errorCode))
      case _ =>
        okResponseWith(PostMessageToChannelResponse(ok = true, error = null))
    }

  private def validateRequestBody(httpRequest: HttpRequest): Option[ErrorCode] = {
    val requestBody = httpRequest.entity.extractAs[PostMessageToChannelRequest]
    if (!channels.contains(requestBody.channel))
      Some("channel_not_found")
    else
      None
  }

  private def okResponseWith(payload: PostMessageToChannelResponse): HttpResponse =
    HttpResponse(
      status = StatusCodes.OK,
      entity = HttpEntity(mapper.marshall(payload))
    )

  private def responseNotFound: HttpResponse =
    HttpResponse(
      status = StatusCodes.NotFound,
      entity = HttpEntity(s"Request's path is not supported in slack Test-kit.")
    )

  private[testkit] val server = aStubWebServer
    .onPort(port)
    .addHandler(handler)
    .build
    .start()
}

