package ua.ucu.edu

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.slf4j.LoggerFactory
import ua.ucu.edu.model._

// dummy app for testing purposes
object DummyStreamingApp extends App {

  val logger = LoggerFactory.getLogger(getClass)

  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streaming_app")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv(Config.KafkaBrokers))
  props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, Long.box(5 * 1000))
  props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, Long.box(0))

  import Serdes._

  implicit val flightTrackerSerde = new FlightTrackerStateSerde
  implicit val weatherStateSerde = new WeatherStateSerde
  implicit val jointStateSerde = new JointStateSerde

  logger.info(s"Waiting for Kafka topics to be created...")
  Thread.sleep(30 * 1000)

  val builder = new StreamsBuilder

  val openSkyStream: KStream[String, FlightTrackerState] = builder.stream[String, FlightTrackerState]("opensky_data")

  openSkyStream.foreach { (k, v) =>
    logger.info(s"record processed $k->$v")
  }


  val weatherStream: KStream[String, WeatherState] = builder.stream[String, WeatherState]("weather_data")

  weatherStream.foreach { (k, v) =>
    logger.info(s"record processed $k->$v")
  }

  val joinedStream: KStream[String, JointState] = openSkyStream.join(weatherStream)(
    (fs, ws) => JointState(fs, ws),
    JoinWindows.of(15000)
  )

  val outStream: KStream[String, JointState] = joinedStream.map((city, state) => (state.callsign, state))

  outStream.foreach { (k, v) =>
    logger.info(s"joined result $k->$v")
  }

  outStream.to("test_topic_out")

  val streams = new KafkaStreams(builder.build(), props)
  streams.cleanUp()
  streams.start()

  sys.addShutdownHook {
    streams.close(10, TimeUnit.SECONDS)
  }

  object Config {
    val KafkaBrokers = "KAFKA_BROKERS"
  }

}
