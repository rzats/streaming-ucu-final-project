package ua.ucu.edu.model

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

class FlightTrackerStateSerializer() extends Serializer[FlightTrackerState] {
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: FlightTrackerState): Array[Byte] =
    mapper.writeValueAsBytes(data)

  override def close(): Unit = {}
}

class FlightTrackerStateDeserializer() extends Deserializer[FlightTrackerState] {
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): FlightTrackerState = data match {
    case null => null
    case _ =>
      try {
        mapper.readValue(data, classOf[FlightTrackerState])
      } catch {
        case _: Exception => null
      }
  }

  override def close(): Unit = {}
}

class FlightTrackerStateSerde extends Serde[FlightTrackerState] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def close(): Unit = {}

  override def serializer(): Serializer[FlightTrackerState] = new FlightTrackerStateSerializer()

  override def deserializer(): Deserializer[FlightTrackerState] = new FlightTrackerStateDeserializer()
}
