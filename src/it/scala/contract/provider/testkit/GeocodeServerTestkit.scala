package contract.provider.testkit

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._
import com.wix.e2e.http.RequestHandler
import com.wix.e2e.http.server.WebServerFactory._

class GeocodeServerTestkit(port: Int) {
  private val server = aMockWebServer.onPort(port)
    .build
    .start()

  private val handler: RequestHandler = {
    case GeocodeRequest(uri) => handleGeocodeRequest(uri)
    case _ => responseNotFound
  }

  private def handleGeocodeRequest(uri: Uri): HttpResponse = {
    responseOkWith(GeocodeApiResponseFactory.geocodeResponse)
  }

  private def responseOkWith: String => HttpResponse = payload =>
    HttpResponse(StatusCodes.OK, entity = HttpEntity(payload))

  private def responseNotFound: HttpResponse =
    HttpResponse(StatusCodes.NotFound, entity = HttpEntity("Request's path is not supported in geocode Test-kit"))

  server.appendAll(handler)
}

object GeocodeRequest {
  def unapply(request: HttpRequest): Option[Uri] =
    request match {
      case HttpRequest(HttpMethods.GET, uri@Path("/maps/api/geocode/json"), _, _, _) => Some(uri)
      case _ => None
    }
}