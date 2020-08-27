package com.sonarspin.kafka.client.message

import com.sonarspin.kafka.client.consumer.{ConsumerTransformer, KafkaConsumerType}
import com.sonarspin.kafka.client.producer.KafkaProducerType

sealed trait WorkerSupervisorMessage

final case class Produce(topic: String, msg: String, simpleKafkaProducer: KafkaProducerType) extends WorkerSupervisorMessage

final case class Consume(topic: String, consumer: KafkaConsumerType, transformer: ConsumerTransformer) extends WorkerSupervisorMessage
