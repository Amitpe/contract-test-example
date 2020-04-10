package contract.provider.sync

import contract.json.JsonJacksonMarshaller
import scalaj.http.Http

import scala.util.{Failure, Success, Try}

class HttpSlackClient(baseUrl: String,
                      token: String) extends SlackClient {

  val mapper = new JsonJacksonMarshaller

  override def postMessageToChannel(channelId: String,
                                    text: String): Try[Unit] = {
    val response = Http(baseUrl + "/api/chat.postMessage")
      .header("content-type", "application/json")
      .header("Authorization", s"Bearer $token")
      .postData(mapper.marshall(PostMessageToChannelRequest(channelId, text)))
      .asString

    val responseBody = mapper.unmarshall[PostMessageToChannelResponse](response.body)
    if (!responseBody.ok)
      Failure(HttpSlackClientException(responseBody.error))
    else
      Success()
  }
}

case class PostMessageToChannelRequest(channel: String, text: String)

case class PostMessageToChannelResponse(ok: Boolean, error: String)

case class HttpSlackClientException(message: String) extends RuntimeException(message)