package contract.slack.sync

class SlackClientITReal extends SlackClientContract {
  private val token = TokenProvider.getTokenOrThrow
  override protected val slackClient = new HttpSlackClient("https://slack.com", token)
}

object TokenProvider {
  def getTokenOrThrow: String =
    sys.props.getOrElse(
      key = "slack.token",
      default = throw new RuntimeException(
        "Please provide slack token: -Dslack.token={token}")
    )
}