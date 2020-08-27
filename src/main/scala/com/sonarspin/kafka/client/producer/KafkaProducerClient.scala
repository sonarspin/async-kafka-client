package com.sonarspin.kafka.client.producer

import akka.actor.typed.ActorRef
import com.sonarspin.kafka.client.json.JsonFormatter
import com.sonarspin.kafka.client.message.{Produce, WorkerSupervisorMessage}

class KafkaProducerClient(producerType: KafkaProducerType, ref: ActorRef[WorkerSupervisorMessage]) {

  def produce[A](topic: String, msg: A)(implicit formatter: JsonFormatter[A]): Unit = {
    ref ! Produce(topic, formatter.toJsonString(msg), producerType)
  }

}

object KafkaProducerClient {

  def apply[B](producerType: KafkaProducerType, ref: ActorRef[WorkerSupervisorMessage]): KafkaProducerClient = {
    new KafkaProducerClient(producerType, ref)
  }
}
