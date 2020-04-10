package contract.provider.testkit

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, _}
import com.wix.e2e.http.client.sync._
import com.wix.e2e.http.matchers.RequestMatchers
import com.wix.e2e.http.server.WebServerFactory._
import com.wix.e2e.http.{HttpRequest, RequestHandler}
import contract.json.{DefaultObjectMapper, JsonJacksonMarshaller}
import contract.provider.sync.{PostMessageToChannelRequest, PostMessageToChannelResponse}
import org.specs2.matcher.Matcher
import org.specs2.mutable.Specification

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

object PostMessageToChannelRequest {
  def unapply(request: HttpRequest): Option[HttpRequest] =
    request match {
      case req@HttpRequest(HttpMethods.POST, uri@Path("/api/chat.postMessage"), _, _, _) => Some(req)
      case _ => None
    }
}

trait SlackMatchers extends Specification with RequestMatchers {
  private val mapper = new DefaultObjectMapper

  def haveReceivedPostMessageRequest(withPayload: PostMessageToChannelRequest): Matcher[FakeSlackServer] = {
    fakeSlack: FakeSlackServer =>
      fakeSlack.server.recordedRequests must contain(
        aRequestWith(HttpMethods.POST, "/api/chat.postMessage", mapper.writeValueAsString(withPayload))
      )
  }

  private def aRequestWith(method: HttpMethod,
                           path: String,
                           withPayload: String): Matcher[HttpRequest] = {
    request: HttpRequest =>
      request.method mustEqual method and
        (request.uri.toString must contain(path)) and
        (request.entity.extractAsString mustEqual withPayload)
  }
}
