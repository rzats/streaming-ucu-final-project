package ua.ucu.edu.common

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

class SimpleJsonSerializer[T >: Null]() extends Serializer[T] {
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: T): Array[Byte] =
    mapper.writeValueAsBytes(data)

  override def close(): Unit = {}
}

class SimpleJsonDeserializer[T >: Null]() extends Deserializer[T] {
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): T = data match {
    case null => null
    case _ =>
      try {
        mapper.readValue(data, classOf[T])
      } catch {
        case _: Exception => null
      }
  }

  override def close(): Unit = {}
}

class SimpleJsonSerde[T >: Null] extends Serde[T] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def close(): Unit = {}

  override def serializer(): Serializer[T] = new SimpleJsonSerializer[T]()

  override def deserializer(): Deserializer[T] = new SimpleJsonDeserializer[T]()
}

