package com.thiefspin.kafka.client.producer

trait SimpleKafkaProducer {
  def produce(topic: String, msg: String)
}
