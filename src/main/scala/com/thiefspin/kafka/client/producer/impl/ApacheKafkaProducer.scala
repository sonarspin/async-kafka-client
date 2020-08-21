package com.thiefspin.kafka.client.producer.impl

import java.util.Properties

import com.thiefspin.kafka.client.producer.SimpleKafkaProducer
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

class ApacheKafkaProducer(props: Option[Properties], servers: Option[String]) {
  lazy val producer: KafkaProducer[String, String] = props match {
    case Some(p) => new KafkaProducer[String, String](p)
    case None => new KafkaProducer[String, String](defaultProps(servers.getOrElse("")))
  }

  val simpleKafkaProducer = new SimpleKafkaProducer {
    override def produce(topic: String, msg: String): Unit = {
      producer.send(new ProducerRecord[String, String](topic, msg), new Callback {
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
          println("completed")
        }
      })
    }
  }

  def serializer = "org.apache.kafka.common.serialization.StringSerializer"

  def defaultProps(servers: String): Properties = {
    val props: Properties = new Properties()
    props.put("bootstrap.servers", servers)
    props.put("key.serializer", serializer)
    props.put("value.serializer", serializer)
    props.put("acks", "1")
    props
  }
}

object ApacheKafkaProducer {

  def apply(props: Option[Properties], servers: Option[String]): ApacheKafkaProducer = {
    new ApacheKafkaProducer(props, servers)
  }
}
