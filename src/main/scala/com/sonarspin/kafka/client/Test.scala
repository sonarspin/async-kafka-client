package com.sonarspin.kafka.client

import java.util.UUID

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import akka.kafka.ConsumerSettings
import com.sonarspin.kafka.client.consumer.StringTransformer
import com.sonarspin.kafka.client.consumer.impl.AkkaStreamKafkaConsumer
import com.sonarspin.kafka.client.json.JsonFormatter
import com.sonarspin.kafka.client.mock.MockKafkaProducer
import com.sonarspin.kafka.client.producer.impl.ApacheKafkaProducer
import com.thiefspin.kafka.client.consumer.StringTransformer
import com.thiefspin.kafka.client.producer.KafkaProducerClient
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json.{Json, OFormat}

object Test extends App {

  implicit val system = ActorSystem("test_system")
  implicit val typedSystem = system.toTyped

  val producerClient = MockKafkaProducer { (topic, msg) =>
    println(topic)
    println(msg)
  }

  val realP = ApacheKafkaProducer(None, Option("192.168.100.29:9092"))(None)

  //  val client = KafkaProducerClient(producerClient.mockProducer, typedSystem)

  val client = AsyncKafkaClient(typedSystem)

  val producer = client.producerInstance("192.168.100.29:9092")

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("192.168.100.29:9092")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    .withClientId(UUID.randomUUID().toString)
    .withGroupId("test-group")

  val transformer = StringTransformer { msg =>
    println(msg)
  }

  val consumer = AkkaStreamKafkaConsumer(consumerSettings)(typedSystem)

  case class User(name: String)

  //implicit val uFormat: OFormat[User] = Json.format[User]

  val topic = "test-topic"

  //  implicit val format = new JsonFormatter[User] {
  //    override def toJsonString(entity: User): String = Json.toJson(entity).toString
  //
  //    override def fromJsonString(json: String): Option[User] = Json.parse(json).validate[User].asOpt
  //  }

  client.consume(topic, consumer.consumer)(transformer)

  //  producer.produce(topic, User("jannie"))
  //  producer.produce(topic, User("pieter"))
  //  producer.produce(topic, User("frikkie"))

}
