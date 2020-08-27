package com.sonarspin.kafka.client.message

import com.sonarspin.kafka.client.producer.KafkaProducerType

sealed trait ProducerWorkerMessage

final case class ProduceToTopic(topic: String, msg: String, simpleKafkaProducer: KafkaProducerType) extends ProducerWorkerMessage
