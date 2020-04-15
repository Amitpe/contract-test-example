package contract.slack.sync

import contract.slack.testkit.FakeSlackServer
import org.specs2.specification.BeforeAll

import scala.util.Random

class SlackClientIT extends SlackClientContract with BeforeAll {

  private val port = 11111
  private val token = Random.nextString(10)

  override protected val existingChannels = Seq(aChannel)
  override protected val baseUrl = s"http://localhost:$port"

  private val fakeSlackServer = new FakeSlackServer(port, token, existingChannels)

  override protected val slackClient = new HttpSlackClient(
    baseUrl = s"http://localhost:$port", token)

  override def beforeAll(): Unit =
    fakeSlackServer.server.start()

  private def aChannel: String = aRandomString
}
