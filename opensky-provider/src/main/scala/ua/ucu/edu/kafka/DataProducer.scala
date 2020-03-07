package ua.ucu.edu.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.slf4j.{Logger, LoggerFactory}
import ua.ucu.edu.api.FlightTrackerApi
import ua.ucu.edu.model.FlightTrackerState

object DataProducer {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def pushFlightTrackerStates(): Unit = {
    val BrokerList: String = System.getenv(Config.KafkaBrokers)
    val Topic = "opensky_data"

    val props = new Properties()
    props.put("bootstrap.servers", BrokerList)
    props.put("client.id", "opensky-provider")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "ua.ucu.edu.model.FlightTrackerStateSerializer")
    logger.info("initializing producer")

    val producer = new KafkaProducer[String, FlightTrackerState](props)

    logger.info("initializing flight tracker API")

    val flightTrackerApi = new FlightTrackerApi

    while (true) {
      val states = flightTrackerApi.getStates
      for (state <- states) {
        logger.info(s"[$Topic] ${state.city} -> $state")
        val data = new ProducerRecord[String, FlightTrackerState](Topic, FlightTrackerApi.cities.keys.toList.indexOf(state.city), state.city, state)
        producer.send(data)
      }
    }

    producer.close()
  }
}

object Config {
  val KafkaBrokers = "KAFKA_BROKERS"
}