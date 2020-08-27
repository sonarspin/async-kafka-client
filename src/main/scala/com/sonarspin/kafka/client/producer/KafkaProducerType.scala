package com.sonarspin.kafka.client.producer

trait KafkaProducerType {
  def produce(topic: String, msg: String)
}
