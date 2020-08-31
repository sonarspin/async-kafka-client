package com.sonarspin.kafka.client

import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.{ActorRef, ActorSystem}
import com.sonarspin.kafka.client.actor.WorkerSupervisor
import com.sonarspin.kafka.client.consumer.{ConsumerTransformer, KafkaConsumerType, SimpleKafkaConsumer, StringTransformer, TypedTransformer}
import com.sonarspin.kafka.client.json.JsonFormatter
import com.sonarspin.kafka.client.message.{Consume, WorkerSupervisorMessage}
import com.sonarspin.kafka.client.producer.{KafkaProducerClient, KafkaProducerType, SimpleKafkaProducerClient}

/**
 * Usages:
 *
 * {{{
 * import akka.actor.ActorSystem
 * import com.thiefspin.kafka.client.AsyncKafkaClient
 * import akka.actor.typed.scaladsl.adapter._
 *
 * object Main extends App {
 *  implicit val system = ActorSystem("tester_system")
 * }
 *
 * val client = AsyncKafkaClient(system.toTyped)
 * val server = "localhost:9092"
 * val topic = "test-topic"
 *
 * case class User(name: String)
 *
 * implicit val uFormat: OFormat[User] = Json.format[User]
 *
 * val producer = client.producerInstance(server)
 *
 * (1 until 10).map { i =>
 *       producer.produce(topic, User(s"User $i"))
 * }
 *
 * client.consume(topic, server, "test-group") { msg =>
 *     logger.info(msg)
 *   }(system.toTyped)
 *
 * }}}
 *
 * @param ref
 */

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
   * Creates a new Kafka consumer running async in the given ExecutionContext.
   *
   * @param topic         Kafka topic
   * @param servers       Kafka address. Example: http://localhost:9092
   * @param consumerGroup Consumer Group passed to the consumer
   * @param f             Function applied to Kafka message in string format
   * @param system        implicit ActorSystem for default Akka streams implementation [[com.sonarspin.kafka.client.consumer.KafkaConsumerType]]
   * @tparam A
   */

  def consume[A](topic: String, servers: String, consumerGroup: String)
                (f: String => Unit)(implicit system: ActorSystem[A]): Unit = {
    ref ! Consume(topic, new SimpleKafkaConsumer(servers, consumerGroup).instance.consumer, StringTransformer(f))
  }

  /**
   *
   * @param topic         Kafka topic
   * @param servers       Kafka address. Example: http://localhost:9092
   * @param consumerGroup Consumer Group passed to the consumer
   * @param f             Function applied to Kafka message in typed format
   * @param system        implicit ActorSystem for default Akka streams implementation [[com.sonarspin.kafka.client.consumer.KafkaConsumerType]]
   * @param formatter     Json formatter for deserialization
   * @tparam A      ActorSystem Type
   * @tparam Entity Kafka message entity schema
   */

  def consume[A, Entity](topic: String, servers: String, consumerGroup: String)
                        (f: Entity => Unit)
                        (implicit system: ActorSystem[A], formatter: JsonFormatter[Entity]): Unit = {
    ref ! Consume(topic, new SimpleKafkaConsumer(servers, consumerGroup).instance.consumer, TypedTransformer(f))
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
