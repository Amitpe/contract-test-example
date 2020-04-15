package contract.slack.sync

import org.specs2.mutable.SpecificationWithJUnit

import scala.util.Random

abstract class SlackClientContract extends SpecificationWithJUnit {

  type ChannelID = String

  protected val baseUrl: String
  protected val existingChannels: Seq[ChannelID]
  protected val slackClient: SlackClient

  "SlackClient" should {

    "successfully post a message to channel" in {
      slackClient.postMessageToChannel(
        channelId = existingChannels.head,
        text = aRandomString) must beSuccessfulTry
    }

    "return failure when providing a wrong token" in {
      val slackClient = new HttpSlackClient(baseUrl, token = aRandomString)

      slackClient.postMessageToChannel(
        channelId = existingChannels.head,
        text = aRandomString) must beFailedTry
    }

    "return failure when trying to post to a non existing channel" in {
      slackClient.postMessageToChannel(
        channelId = aRandomString,
        text = aRandomString) must beFailedTry
    }

  }

  protected def aRandomString =
    Random.alphanumeric.dropWhile(_.isDigit).take(10).mkString
}
