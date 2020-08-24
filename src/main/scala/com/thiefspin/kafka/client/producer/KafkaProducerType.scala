package com.thiefspin.kafka.client.producer

trait KafkaProducerType {
  def produce(topic: String, msg: String)
}
