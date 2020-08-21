package com.thiefspin.kafka.client.message

import com.thiefspin.kafka.client.producer.SimpleKafkaProducer

sealed trait ProducerWorkerMessage

final case class ProduceToTopic(topic: String, msg: String, simpleKafkaProducer: SimpleKafkaProducer) extends ProducerWorkerMessage
