package com.thiefspin.kafka.client

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import com.thiefspin.kafka.client.json.JsonFormatter
import com.thiefspin.kafka.client.mock.MockKafkaProducer

object Test extends App {

  implicit val system = ActorSystem("test_system")
  implicit val typedSystem = system.toTyped

  val client = SimpleKafkaClient("localhost:9092", typedSystem)

  case class User(name: String)

  implicit val format = new JsonFormatter[User] {
    override def toJsonString(entity: User): String = entity.name
  }

  val producer = MockKafkaProducer { (topic, msg) =>
    println(topic)
    println(msg)
  }

  client.produce("topic1", User("jannie"), producer.simpleKafkaProducer)
  client.produce("topic2", User("piter"), producer.simpleKafkaProducer)
  client.produce("topic3", User("frikkie"), producer.simpleKafkaProducer)

}
