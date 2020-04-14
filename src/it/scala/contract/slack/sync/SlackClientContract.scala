package contract.slack.sync

import org.specs2.mutable.SpecificationWithJUnit

abstract class SlackClientContract extends SpecificationWithJUnit {

  protected val slackClient: SlackClient

  "SlackClient" should {

    "successfully post a message to channel" in {
      val channelId = "C97NS35J9"
      val text = "Hello, world!"

      slackClient.postMessageToChannel(channelId, text) must beSuccessfulTry
    }

    "return failure when providing a wrong token" in {
      pending("not implemented yet")
    }

    "return failure when trying to post to a non existing channel" in {
      pending("not implemented yet")
    }

  }
}
