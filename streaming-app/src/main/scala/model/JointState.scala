package model

import ua.ucu.edu.model.FlightTrackerState

case class JointState (
  // flight tracker part
  timestamp: Int,
  latitude: Double,
  longitude: Double,
  city: String, // e.g. "London", "Munich", "Warsaw"
  callsign: String, // identifier e.g. LOT-1234
  originCountry: String, // self-explanatory
  // weather part
  hot: String
) {
  def apply(fs: FlightTrackerState, ws: String): JointState = JointState(
    fs.timestamp,
    fs.latitude,
    fs.longitude,
    fs.city,
    fs.callsign,
    fs.originCountry,
    ws
  )
}
