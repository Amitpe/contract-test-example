package contract.provider.sync

import contract.provider.testkit.FakeSlackServer
import org.specs2.mutable.SpecificationWithJUnit

import scala.util.Random

class SlackClientIT extends SpecificationWithJUnit {

  private val port = 11111
  private val token = "****"

  val channels = Seq("C97NS35J9")
  private val slackTestkit = new FakeSlackServer(port, token, channels)
  private val slackClient = new HttpSlackClient(s"http://localhost:$port", token)

  "Slack Client" should {
    "successfully post message to channel" in {
      val channelId = channels.head
      val text = "Message Text"

      slackClient.postMessageToChannel(channelId, text) must beSuccessfulTry
    }

    "fail when posting into a non existing channel" in {
      val channelId = aNonExistingChannel
      val text = "Message Text"

      slackClient.postMessageToChannel(channelId, text) must beFailedTry
    }

    "fail when using wrong token" in {
      val slackClient = new HttpSlackClient(s"http://localhost:$port", "bad token")

      val channelId = channels.head
      val text = "Message Text"

      slackClient.postMessageToChannel(channelId, text) must beFailedTry
    }
  }

  def aNonExistingChannel: String = Random.nextString(9)
}
