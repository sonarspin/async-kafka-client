package com.thiefspin.kafka.client

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.ActorContext
import com.thiefspin.kafka.client.actor.WorkerSupervisor
import com.thiefspin.kafka.client.consumer.impl.AkkaStreamKafkaConsumer
import com.thiefspin.kafka.client.consumer.{ConsumerTransformer, KafkaConsumerType, SimpleKafkaConsumer, StringTransformer}
import com.thiefspin.kafka.client.message.{Consume, WorkerSupervisorMessage}
import com.thiefspin.kafka.client.producer.{KafkaProducerClient, KafkaProducerType, SimpleKafkaProducerClient}

class AsyncKafkaClient(ref: ActorRef[WorkerSupervisorMessage]) {

  def producerInstance(producerType: KafkaProducerType): KafkaProducerClient = {
    KafkaProducerClient(producerType, ref)
  }

  def producerInstance(servers: String): KafkaProducerClient = {
    SimpleKafkaProducerClient(servers, ref)
  }

  def consume(topic: String, consumer: KafkaConsumerType)(transformer: ConsumerTransformer): Unit = {
    ref ! Consume(topic, consumer, transformer)
  }

  def consume[A](topic: String, servers: String, consumerGroup: String)(f: String => Unit)(implicit system: ActorSystem[A]): Unit = {
    ref ! Consume(topic, new SimpleKafkaConsumer(servers, consumerGroup).instance.consumer, StringTransformer(f))
  }

}

object AsyncKafkaClient {

  def apply[A](implicit context: ActorContext[A]): AsyncKafkaClient = {
    new AsyncKafkaClient(WorkerSupervisor(context))
  }

  def apply[A](implicit system: ActorSystem[A]): AsyncKafkaClient = {
    new AsyncKafkaClient(WorkerSupervisor(system))
  }
}
