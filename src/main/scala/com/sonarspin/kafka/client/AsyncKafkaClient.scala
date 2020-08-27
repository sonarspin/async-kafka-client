package com.sonarspin.kafka.client

import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.{ActorRef, ActorSystem}
import com.sonarspin.kafka.client.actor.WorkerSupervisor
import com.sonarspin.kafka.client.consumer.{ConsumerTransformer, KafkaConsumerType, SimpleKafkaConsumer, StringTransformer}
import com.sonarspin.kafka.client.message.{Consume, WorkerSupervisorMessage}
import com.sonarspin.kafka.client.producer.{KafkaProducerClient, KafkaProducerType, SimpleKafkaProducerClient}

class AsyncKafkaClient(ref: ActorRef[WorkerSupervisorMessage]) {

  /**
   * Creates a new instance of the [[com.sonarspin.kafka.client.producer.KafkaProducerClient]].
   *
   * @param producerType What underlying producer to use. Example [[com.sonarspin.kafka.client.producer.impl.ApacheKafkaProducer]]
   * @return New instance of [[com.sonarspin.kafka.client.producer.KafkaProducerClient]].
   */

  def producerInstance(producerType: KafkaProducerType): KafkaProducerClient = {
    producer.KafkaProducerClient(producerType, ref)
  }

  /**
   * Creates a new instance of the [[com.sonarspin.kafka.client.producer.SimpleKafkaProducerClient]].
   *
   * @param servers Kafka address. Example: http://localhost:9092
   * @return [[com.sonarspin.kafka.client.producer.SimpleKafkaProducerClient]]
   */

  def producerInstance(servers: String): SimpleKafkaProducerClient = {
    producer.SimpleKafkaProducerClient(servers, ref)
  }

  /**
   * Creates a new Kafka consumer running async in the given ExecutionContext.
   *
   * @param topic       Kafka topic
   * @param consumer    Type of Kafka consumer. Example: [[com.sonarspin.kafka.client.consumer.impl.AkkaStreamKafkaConsumer]]
   * @param transformer Transformer function. [[com.sonarspin.kafka.client.consumer.ConsumerTransformer]]
   */

  def consume(topic: String, consumer: KafkaConsumerType)(transformer: ConsumerTransformer): Unit = {
    ref ! Consume(topic, consumer, transformer)
  }

  /**
   *
   * @param topic         Kafka topic
   * @param servers       Kafka address. Example: http://localhost:9092
   * @param consumerGroup Consumer Group passed to the consumer
   * @param f             Function applied to Kafka message in string format
   * @param system        implicit ActorSystem for default Akka streams implementation [[com.sonarspin.kafka.client.consumer.KafkaConsumerType]]
   * @tparam A
   */

  def consume[A](topic: String, servers: String, consumerGroup: String)(f: String => Unit)(implicit system: ActorSystem[A]): Unit = {
    ref ! Consume(topic, new SimpleKafkaConsumer(servers, consumerGroup).instance.consumer, StringTransformer(f))
  }

}

object AsyncKafkaClient {

  /**
   * Creates a new instance of the async Kafka client.
   *
   * @param context ActorContext[A]
   * @tparam A Type Parameter
   * @return a new instance of [[com.sonarspin.kafka.client.AsyncKafkaClient]]
   */

  def apply[A](implicit context: ActorContext[A]): AsyncKafkaClient = {
    new AsyncKafkaClient(WorkerSupervisor(context))
  }

  /**
   * Creates a new instance of the async Kafka client.
   *
   * @param system ActorSystem[A]
   * @tparam A Type Parameter
   * @return a new instance of [[com.sonarspin.kafka.client.AsyncKafkaClient]]
   */

  def apply[A](implicit system: ActorSystem[A]): AsyncKafkaClient = {
    new AsyncKafkaClient(WorkerSupervisor(system))
  }
}
