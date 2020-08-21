package com.thiefspin.kafka.client

import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.{ActorRef, ActorSystem}
import com.thiefspin.kafka.client.actor.WorkerSupervisor
import com.thiefspin.kafka.client.json.JsonFormatter
import com.thiefspin.kafka.client.message.{Produce, WorkerSupervisorMessage}
import com.thiefspin.kafka.client.producer.SimpleKafkaProducer
import com.thiefspin.kafka.client.producer.impl._

class SimpleKafkaClient(kafkaServers: String, ref: ActorRef[WorkerSupervisorMessage]) {

  private lazy val apacheProducer = ApacheKafkaProducer(None, Option(kafkaServers))

  def produce[A](topic: String, msg: A, producerType: SimpleKafkaProducer = apacheProducer.simpleKafkaProducer)(implicit formatter: JsonFormatter[A]): Unit = {
    ref ! Produce(topic, formatter.toJsonString(msg), producerType)
  }

}

object SimpleKafkaClient {

  def apply[B](kafkaBootstrapServers: String, system: ActorSystem[B]): SimpleKafkaClient = {
    new SimpleKafkaClient(kafkaBootstrapServers, WorkerSupervisor(system))
  }

  def apply[B](kafkaBootstrapServers: String, context: ActorContext[B]): SimpleKafkaClient = {
    new SimpleKafkaClient(kafkaBootstrapServers, WorkerSupervisor(context))
  }
}
