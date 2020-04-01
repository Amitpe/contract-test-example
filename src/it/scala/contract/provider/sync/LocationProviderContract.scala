package contract.provider.sync

import contract.domain.{Location, LocationProviderResponse}
import contract.provider.testkit.GeocodeServerTestkit
import org.specs2.mutable.SpecificationWithJUnit

abstract class LocationProviderContract extends SpecificationWithJUnit {

  protected val baseUrl: String
  protected val apiKey: String

  private lazy val provider = new GeocodeLocationProvider(baseUrl, apiKey)

  "provide address by 5 digit zip code" in {
    val zipCode = "90210"
    val US = "US"
    val CALIFORNIA = "CA"
    val expectedLocation = Location(zipCode, US, CALIFORNIA)

    val searchResult = provider.searchByZipCode(zipCode)

    searchResult must beEqualTo(LocationProviderResponse(Seq(expectedLocation)))
  }
}

class LocationProviderITReal extends LocationProviderContract {
  override val baseUrl = "https://maps.googleapis.com"
  override val apiKey = "AIzaSyCWeeateTaYGqsHhNcmoDfT7Us-vLDZVPs"
}

class LocationProviderIT extends LocationProviderContract {
  private val port = 11111
  private val geocodeTestkit = new GeocodeServerTestkit(port)

  val baseUrl = s"http://localhost:$port"
  val apiKey = "fake-api-key"
}
