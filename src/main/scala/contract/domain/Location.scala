package contract.domain

case class LocationProviderResponse(locations: Seq[Location])

case class Location(zipCode: String,
                    countryCode: String,
                    stateCode: String)