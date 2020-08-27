package com.sonarspin.kafka.client.consumer

import java.util.UUID

import akka.actor.typed.ActorSystem
import akka.kafka.ConsumerSettings
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import akka.actor.typed.scaladsl.adapter._
import com.sonarspin.kafka.client.consumer.impl.AkkaStreamKafkaConsumer

class SimpleKafkaConsumer(servers: String, consumerGroup: String)(implicit system: ActorSystem[_]) {

  val instance: AkkaStreamKafkaConsumer = AkkaStreamKafkaConsumer(settings)(system)

  private def settings: ConsumerSettings[String, String] = {
    ConsumerSettings(system.toClassic, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(servers)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
      .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
      .withClientId(UUID.randomUUID().toString)
      .withGroupId(consumerGroup)
  }

}
