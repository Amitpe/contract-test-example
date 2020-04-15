package contract.slack.sync

import contract.slack.testkit.FakeSlackServer
import org.specs2.specification.BeforeAll

import scala.util.Random

class SlackClientIT extends SlackClientContract with BeforeAll {

  private val port = 11111
  private val fakeSlackServer = new FakeSlackServer(port)

  private val token = Random.nextString(10)
  override protected val slackClient = new HttpSlackClient(
    baseUrl = s"http://localhost:$port", token)

  override def beforeAll(): Unit = fakeSlackServer.server.start()
}
