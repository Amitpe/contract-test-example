package contract.provider.sync

import contract.domain.LocationProviderResponse

import scala.concurrent.Future

trait LocationProvider {
  def searchByZipCode(zipCode: String): LocationProviderResponse
}
