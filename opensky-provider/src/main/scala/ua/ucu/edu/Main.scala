package ua.ucu.edu

import ua.ucu.edu.kafka.DataProducer

object Main extends App {
  DataProducer.pushFlightTrackerStates()
}
