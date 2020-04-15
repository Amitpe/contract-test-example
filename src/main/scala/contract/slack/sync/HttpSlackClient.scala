package contract.slack.sync

import contract.json.DefaultObjectMapper
import scalaj.http.Http

import scala.util.{Success, Try}

class HttpSlackClient(baseUrl: String,
                      token: String) extends SlackClient {

  private val mapper = new DefaultObjectMapper

  override def postMessageToChannel(channelId: String,
                                    text: String): Try[Unit] = {
    val response = Http(baseUrl + "/api/chat.postMessage")
      .header("content-type", "application/json")
      .header("Authorization", s"Bearer $token")
      .postData(s"""{ "channel": "$channelId", "text": "$text" }""")
      .asString

    val responseBody = mapper.readValue(response.body, classOf[PostMessageToChannelResponse])

    if (responseBody.ok)
      Success()
    else
      throw new RuntimeException("Kaboom")
  }
}

case class PostMessageToChannelResponse(ok: Boolean, error: String)


case class PostMessageToChannelRequest(channel: String, text: String)

case class HttpSlackClientException(message: String) extends RuntimeException(message)