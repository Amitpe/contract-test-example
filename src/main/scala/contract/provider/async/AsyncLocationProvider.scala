package contract.provider.async

import contract.domain.LocationProviderResponse

import scala.concurrent.Future

trait AsyncLocationProvider {
  def searchByZipCode(zipCode: String): Future[LocationProviderResponse]
}
