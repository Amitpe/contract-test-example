package contract.provider.sync

import contract.provider.testkit.FakeSlackServer
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.BeforeAll

import scala.util.Random

class SlackClientIT extends SpecificationWithJUnit with BeforeAll {

  private val port = 11111
  private val token = "****"
  private val channels = Seq("C97NS35J9")

  private val fakeSlackServer = new FakeSlackServer(port, token, channels)
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

  def aNonExistingChannel: String =
    Random.nextString(9)

  override def beforeAll(): Unit =
    fakeSlackServer.server.start()
}
