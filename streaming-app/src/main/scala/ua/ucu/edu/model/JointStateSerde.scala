package ua.ucu.edu.model

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

class JointStateSerializer() extends Serializer[JointState] {
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: JointState): Array[Byte] =
    mapper.writeValueAsBytes(data)

  override def close(): Unit = {}
}

class JointStateDeserializer() extends Deserializer[JointState] {
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): JointState = data match {
    case null => null
    case _ =>
      try {
        mapper.readValue(data, classOf[JointState])
      } catch {
        case _: Exception => null
      }
  }

  override def close(): Unit = {}
}

class JointStateSerde extends Serde[JointState] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def close(): Unit = {}

  override def serializer(): Serializer[JointState] = new JointStateSerializer()

  override def deserializer(): Deserializer[JointState] = new JointStateDeserializer()
}
