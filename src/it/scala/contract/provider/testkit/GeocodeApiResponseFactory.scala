package contract.provider.testkit

object GeocodeApiResponseFactory {
val geocodeResponse =
  s"""
     |{
     |   "results": [
     |      {
     |         "address_components": [
     |            {
     |               "long_name": "Beverly Hills",
     |               "short_name": "Beverly Hills",
     |               "types": [
     |                  "locality",
     |                  "political"
     |               ]
     |            },
     |            {
     |               "long_name": "Los Angeles County",
     |               "short_name": "Los Angeles County",
     |               "types": [
     |                  "administrative_area_level_2",
     |                  "political"
     |               ]
     |            },
     |            {
     |               "long_name": "California",
     |               "short_name": "CA",
     |               "types": [
     |                  "administrative_area_level_1",
     |                  "political"
     |               ]
     |            },
     |            {
     |               "long_name": "United States",
     |               "short_name": "US",
     |               "types": [
     |                  "country",
     |                  "political"
     |               ]
     |            },
     |            {
     |               "long_name": "90210",
     |               "short_name": "90210",
     |               "types": [
     |                  "postal_code"
     |               ]
     |            }
     |         ],
     |         "formatted_address": "338 N Canon Dr (2nd and 3rd Floor, Beverly Hills, CA 90210, USA",
     |         "geometry": {
     |            "location": {
     |               "lat": 34.07018,
     |               "lng": -118.399956
     |            },
     |            "location_type": "GEOMETRIC_CENTER",
     |            "viewport": {
     |               "northeast": {
     |                  "lat": 34.0715289802915,
     |                  "lng": -118.3986070197085
     |               },
     |               "southwest": {
     |                  "lat": 34.0688310197085,
     |                  "lng": -118.4013049802915
     |               }
     |            }
     |         },
     |         "place_id": "ChIJ_SoTXge8woAR5pPtL6J1enM",
     |         "plus_code": {
     |            "compound_code": "3JC2+32 Beverly Hills, California, United States",
     |            "global_code": "85633JC2+32"
     |         },
     |         "types": [
     |            "beauty_salon",
     |            "establishment",
     |            "hair_care",
     |            "health",
     |            "point_of_interest"
     |         ]
     |      }
     |   ],
     |   "status": "OK"
     |}
     |""".stripMargin
}
