package com.sonarspin.kafka.client.mock

import com.sonarspin.kafka.client.producer.KafkaProducerType

class MockKafkaProducer(f: (String, String) => Unit) {
  val mockProducer = new KafkaProducerType {
    override def produce(topic: String, msg: String): Unit = {
      f(topic, msg)
    }
  }
}

object MockKafkaProducer {
  def apply(f: (String, String) => Unit): MockKafkaProducer = {
    new MockKafkaProducer(f)
  }
}
