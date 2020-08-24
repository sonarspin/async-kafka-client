package com.thiefspin.kafka.client

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import com.thiefspin.kafka.client.json.JsonFormatter
import com.thiefspin.kafka.client.mock.MockKafkaProducer
import com.thiefspin.kafka.client.producer.KafkaProducerClient

object Test extends App {

  implicit val system = ActorSystem("test_system")
  implicit val typedSystem = system.toTyped

  val producerClient = MockKafkaProducer { (topic, msg) =>
    println(topic)
    println(msg)
  }

  val client = KafkaProducerClient(producerClient.mockProducer, typedSystem)

  case class User(name: String)

  implicit val format = new JsonFormatter[User] {
    override def toJsonString(entity: User): String = entity.name
  }

//  client.produce("topic1", User("jannie"), producer.simpleKafkaProducer)
//  client.produce("topic2", User("piter"), producer.simpleKafkaProducer)
//  client.produce("topic3", User("frikkie"), producer.simpleKafkaProducer)

  client.produce("topic1", User("jannie"))
  client.produce("topic2", User("piter"))
  client.produce("topic3", User("frikkie"))

}
