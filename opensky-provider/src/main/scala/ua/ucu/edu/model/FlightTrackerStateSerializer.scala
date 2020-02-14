package ua.ucu.edu.model

import java.util

import com.google.gson.Gson
import org.apache.kafka.common.serialization.Serializer

class FlightTrackerStateSerializer extends Serializer[FlightTrackerState] {
  private val gson: Gson = new Gson()

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: FlightTrackerState): Array[Byte] = {
    if (data == null)
      null
    else
      gson.toJson(data).getBytes
  }

  override def close(): Unit = {}

}
