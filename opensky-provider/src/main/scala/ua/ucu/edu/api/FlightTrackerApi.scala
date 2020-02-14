package ua.ucu.edu.api

import org.opensky.api.OpenSkyApi
import org.opensky.api.OpenSkyApi.BoundingBox
import org.opensky.model.OpenSkyStates
import org.slf4j.{Logger, LoggerFactory}
import ua.ucu.edu.kafka.DummyDataProducer.getClass
import ua.ucu.edu.model.FlightTrackerState

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

class FlightTrackerApi {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  // Free API instance
  val api = new OpenSkyApi("rzatserkovnyi", "Bh2LZa6Wim3mUXF")

  // London, Munich, Warsaw, Prague, Paris, Brussels, Amsterdam, Madrid, Barcelona, Rome
  val bbox = new BoundingBox(49, 54, 14, 25)
  val cities: Map[String, BoundingBox] = Map(
    "London" -> new BoundingBox(51.1839, 51.8324, -0.7868, 0.4748),
    "Munich" -> new BoundingBox(47.8514, 48.5735, 10.9110, 12.0123),
    "Warsaw" -> new BoundingBox(52.1102, 52.3991, 20.7347, 21.2544),
    "Prague" -> new BoundingBox(49.9390, 50.2680, 13.9633, 14.7355),
    "Paris" -> new BoundingBox(48.5171, 49.2809, 1.7769, 2.8923),
    "Brussels" -> new BoundingBox(50.7413, 51.0238, 4.1184, 4.6702),
    "Amsterdam" -> new BoundingBox(52.1387, 52.5776, 4.5409, 5.0862),
    "Madrid" -> new BoundingBox(40.2039, 40.67134, -4.0480, -3.2057),
    "Barcelona" -> new BoundingBox(41.1627, 41.5670, 1.6084, 2.4836),
    "Rome" -> new BoundingBox(41.5083, 42.1790, 11.9569, 41.5083)
  )

  def getStates: List[FlightTrackerState] = {
    val flightTrackerStates = new ListBuffer[FlightTrackerState]()
    cities.foreach {
      case (city, bbox) =>
        logger.info(s"Calling API for $city")
        val response: OpenSkyStates = api.getStates(0, null, bbox)
        if (response == null)
          logger.info("Got response with 0 states")
        else
          logger.info(s"Got response with ${response.getStates.size} states")
        val cityStates: List[FlightTrackerState] = if (response == null) List() else response.getStates.asScala.toList.map(state => FlightTrackerState(
          response.getTime,
          state.getLatitude,
          state.getLongitude,
          city,
          state.getCallsign,
          state.getOriginCountry
        ))
        logger.info(s"Processed ${cityStates.size} states")
        flightTrackerStates ++= cityStates
        logger.info(s"Total: ${flightTrackerStates.size}")
        Thread.sleep(1 * 1000)
    }
    flightTrackerStates.toList
  }
}
