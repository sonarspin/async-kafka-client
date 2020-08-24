package com.thiefspin.kafka.client.message

import com.thiefspin.kafka.client.producer.KafkaProducerType

sealed trait ProducerWorkerMessage

final case class ProduceToTopic(topic: String, msg: String, simpleKafkaProducer: KafkaProducerType) extends ProducerWorkerMessage
