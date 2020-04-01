package contract.provider.async

import contract.domain.{GeocodeResponse, Location, LocationProviderResponse}
import contract.json.DefaultObjectMapper
import org.asynchttpclient.{DefaultAsyncHttpClient, DefaultAsyncHttpClientConfig}

import scala.concurrent.Future

class GeocodeAsyncLocationProvider extends AsyncLocationProvider {
  private val API_KEY = "AIzaSyCWeeateTaYGqsHhNcmoDfT7Us-vLDZVPs"
  private val mapper = new DefaultObjectMapper()
  private val httpClient = new DefaultAsyncHttpClient(
    new DefaultAsyncHttpClientConfig.Builder()
      .setConnectTimeout(3 * 1000)
      .setRequestTimeout(3 * 1000)
      .build())

  override def searchByZipCode(zipCode: String): Future[LocationProviderResponse] = {
    val geocodeResponse = searchViaGeocodeApi(zipCode)
    val locations = extractLocationsFrom(geocodeResponse)

    Future.successful {
      LocationProviderResponse(
        locations = locations
      )
    }
  }

  private def searchViaGeocodeApi(zipCode: String) = {
    val responseBody = httpClient
      .prepareGet(buildSearchUrlFor(zipCode))
      .execute()
      .get()
      .getResponseBody

    mapper.readValue(responseBody, classOf[GeocodeResponse])
  }

  private def buildSearchUrlFor(zipCode: String) =
    s"https://maps.googleapis.com/maps/api/geocode/json?key=$API_KEY=&components=postal_code:$zipCode&region=us"

  private def extractLocationsFrom(geocodeResponse: GeocodeResponse) = {
    val addressComponents = geocodeResponse.results.head.addressComponents
    val postalCode = addressComponents.find(_.types.contains("postal_code")).get.shortName
    val countryCode = addressComponents.find(_.types.contains("country")).get.shortName
    val stateCode = addressComponents.find(_.types.contains("administrative_area_level_1")).get.shortName
    val locations = Seq(Location(postalCode, countryCode, stateCode))
    locations
  }

}
