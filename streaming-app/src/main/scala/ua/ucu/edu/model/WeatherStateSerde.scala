package ua.ucu.edu.model

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

class WeatherStateSerializer() extends Serializer[WeatherState] {
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: WeatherState): Array[Byte] =
    mapper.writeValueAsBytes(data)

  override def close(): Unit = {}
}

class WeatherStateDeserializer() extends Deserializer[WeatherState] {
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): WeatherState = data match {
    case null => null
    case _ =>
      try {
        mapper.readValue(data, classOf[WeatherState])
      } catch {
        case _: Exception => null
      }
  }

  override def close(): Unit = {}
}

class WeatherStateSerde extends Serde[WeatherState] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def close(): Unit = {}

  override def serializer(): Serializer[WeatherState] = new WeatherStateSerializer()

  override def deserializer(): Deserializer[WeatherState] = new WeatherStateDeserializer()
}
