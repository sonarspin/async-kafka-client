package com.thiefspin.kafka.client.message

import com.thiefspin.kafka.client.producer.KafkaProducerType

sealed trait WorkerSupervisorMessage

final case class Produce(topic: String, msg: String, simpleKafkaProducer: KafkaProducerType) extends WorkerSupervisorMessage

final case class Consume() extends WorkerSupervisorMessage
