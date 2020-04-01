package contract.provider.sync

import contract.domain.{GeocodeResponse, GeocodeResult, Location, LocationProviderResponse}
import contract.json.DefaultObjectMapper
import scalaj.http.Http

class GeocodeLocationProvider(baseUrl: String, apiKey: String) extends LocationProvider {
  private val mapper = new DefaultObjectMapper
  private val geoCodePath = "/maps/api/geocode/json"

  override def searchByZipCode(zipCode: String): LocationProviderResponse = {
    val responseBody = searchViaGeocodeApi(zipCode)
    val geocodeResponse = parseToResponse(responseBody)
    val locations = extractLocationsFrom(geocodeResponse)

    LocationProviderResponse(locations)
  }

  private def searchViaGeocodeApi(zipCode: String): String = {
    Http(baseUrl + geoCodePath)
      .param("key", apiKey)
      .param("components", s"postal_code:$zipCode")
      .asString
      .body
  }

  private def parseToResponse(responseBody: String): GeocodeResponse =
    mapper.readValue(responseBody, classOf[GeocodeResponse])

  private def extractLocationsFrom(geocodeResponse: GeocodeResponse): Seq[Location] = {
    geocodeResponse
      .results
      .headOption
      .map(toLocations)
      .getOrElse(Nil)
  }

  private def toLocations(geocodeResult: GeocodeResult) = {
    val postalCode = findAddressComponentThatOrDefault(geocodeResult, ComponentType.PostalCode)
    val countryCode = findAddressComponentThatOrDefault(geocodeResult, ComponentType.Country)
    val stateCode = findAddressComponentThatOrDefault(geocodeResult, ComponentType.State)
    Seq(Location(postalCode, countryCode, stateCode))
  }

  private def findAddressComponentThatOrDefault(geocodeResult: GeocodeResult,
                                                componentType: String,
                                                default: String = ""): String =
    geocodeResult
      .addressComponents
      .find(_.types.contains(componentType))
      .map(_.shortName)
      .getOrElse(default)
}

object ComponentType {
  val PostalCode = "postal_code"
  val Country = "country"
  val State = "administrative_area_level_1"
}
