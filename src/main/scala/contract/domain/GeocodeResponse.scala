package contract.domain

import com.fasterxml.jackson.annotation.JsonProperty

case class GeocodeResponse(results: Seq[GeocodeResult],
                           status: String)

case class GeocodeResult(@JsonProperty("address_components") addressComponents: Seq[AddressComponent])

case class AddressComponent(@JsonProperty("short_name") shortName: String,
                            types: Seq[String])