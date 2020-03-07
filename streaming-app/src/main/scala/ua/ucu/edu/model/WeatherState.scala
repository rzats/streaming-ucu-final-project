package ua.ucu.edu.model

case class WeatherState(
  weather_main: String,
  weather_description: String,
  temp: Double,
  pressure: Double,
  humidity: Double,
  wind_speed: Double
)
