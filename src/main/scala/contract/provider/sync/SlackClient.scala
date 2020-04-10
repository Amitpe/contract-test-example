package contract.provider.sync

import scala.util.Try

trait SlackClient {
  def postMessageToChannel(channelId: String,
                           text: String): Try[Unit]
}
