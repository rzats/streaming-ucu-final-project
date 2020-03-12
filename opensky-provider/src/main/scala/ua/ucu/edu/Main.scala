package ua.ucu.edu

import ua.ucu.edu.kafka.DataProducer

object Main extends App {
  Thread.sleep(10 * 1000)

  DataProducer.pushFlightTrackerStates()
}
