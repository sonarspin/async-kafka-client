package com.sonarspin.kafka.client.producer

import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.{ActorRef, ActorSystem}
import com.sonarspin.kafka.client.actor.WorkerSupervisor
import com.sonarspin.kafka.client.json.JsonFormatter
import com.sonarspin.kafka.client.message.{Produce, WorkerSupervisorMessage}
import com.thiefspin.kafka.client.message.Produce

class KafkaProducerClient(producerType: KafkaProducerType, ref: ActorRef[WorkerSupervisorMessage]) {

  //private lazy val apacheProducer: ApacheKafkaProducer = ApacheKafkaProducer(None, Option(kafkaServers))(None)

  def produce[A](topic: String, msg: A)(implicit formatter: JsonFormatter[A]): Unit = {
    ref ! Produce(topic, formatter.toJsonString(msg), producerType)
  }

}

object KafkaProducerClient {

  def apply[B](producerType: KafkaProducerType, ref: ActorRef[WorkerSupervisorMessage]): KafkaProducerClient = {
    new KafkaProducerClient(producerType, ref)
  }

//  def apply[B](producerType: KafkaProducerType, system: ActorSystem[B]): KafkaProducerClient = {
//    new KafkaProducerClient(producerType, WorkerSupervisor(system))
//  }
//
//  def apply[B](producerType: KafkaProducerType, context: ActorContext[B]): KafkaProducerClient = {
//    new KafkaProducerClient(producerType, WorkerSupervisor(context))
//  }
}
