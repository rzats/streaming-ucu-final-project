package ua.ucu.edu.model

case class FlightTrackerState(
  timestamp: Int,
  latitude: Double,
  longitude: Double,
  city: String, // e.g. "London", "Munich", "Warsaw"
  callsign: String, // identifier e.g. LOT-1234
  originCountry: String // self-explanatory
)