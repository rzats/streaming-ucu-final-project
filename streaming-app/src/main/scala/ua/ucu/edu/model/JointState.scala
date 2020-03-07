package ua.ucu.edu.model

case class JointState(
  // flight tracker part
  timestamp: Int,
  latitude: Double,
  longitude: Double,
  city: String, // e.g. "London", "Munich", "Warsaw"
  callsign: String, // identifier e.g. LOT-1234
  originCountry: String, // self-explanatory
  // weather part
  weatherMain: String,
  weatherDescription: String,
  temp: Double,
  pressure: Double,
  humidity: Double,
  windSpeed: Double
)

object JointState {
  def apply(fs: FlightTrackerState, ws: WeatherState): JointState = JointState(
    fs.timestamp,
    fs.latitude,
    fs.longitude,
    fs.city,
    fs.callsign,
    fs.originCountry,
    ws.weather_main,
    ws.weather_description,
    ws.temp,
    ws.pressure,
    ws.humidity,
    ws.wind_speed
  )
}
