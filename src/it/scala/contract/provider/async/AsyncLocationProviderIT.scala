package contract.provider.async

import contract.domain.{Location, LocationProviderResponse}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.SpecificationWithJUnit

class AsyncLocationProviderIT(implicit ev: ExecutionEnv) extends SpecificationWithJUnit {

  val provider = new GeocodeAsyncLocationProvider

  "provide address by 5 digit zip code" in {
    val zipCode = "90210"
    val US = "US"
    val CALIFORNIA = "CA"
    val expectedLocation = Location(zipCode, US, CALIFORNIA)

    val searchResult = provider.searchByZipCode(zipCode)

    searchResult must beEqualTo(LocationProviderResponse(Seq(expectedLocation))).await
  }
}