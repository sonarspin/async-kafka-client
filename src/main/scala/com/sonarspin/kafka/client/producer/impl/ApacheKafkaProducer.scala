package com.sonarspin.kafka.client.producer.impl

import java.util.Properties

import com.sonarspin.kafka.client.producer.KafkaProducerType
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

class ApacheKafkaProducer(props: Option[Properties], servers: Option[String], callback: ApacheProducerCallbackParams => Unit) {
  private lazy val apacheProducer: KafkaProducer[String, String] = props match {
    case Some(p) => new KafkaProducer[String, String](p)
    case None => new KafkaProducer[String, String](defaultProps(servers.getOrElse("localhost:9092")))
  }

  val producer = new KafkaProducerType {
    override def produce(topic: String, msg: String): Unit = {
      apacheProducer.send(new ProducerRecord[String, String](topic, msg), new Callback {
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
          callback {
            ApacheProducerCallbackParams(topic, msg, metadata, exception)
          }
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

object ApacheKafkaProducer extends LazyLogging {

  def apply(props: Option[Properties], servers: Option[String])(callback: Option[ApacheProducerCallbackParams => Unit]): ApacheKafkaProducer = {
    new ApacheKafkaProducer(props, servers, callback.getOrElse(defaultCallback))
  }

  private def defaultCallback(callBackParams: ApacheProducerCallbackParams): Unit = {
    if (callBackParams.exception != null) {
      logger.error(s"Could not produce Kafka message: ${callBackParams.msg} to topic: ${callBackParams.topic}. \n" +
        s"Exception: ${callBackParams.exception.getMessage}")
    } else {
      logger.info(s"Kafka message sent: ${callBackParams.msg} to topic: ${callBackParams.topic}. Record meta data: ${callBackParams.recordMetadata.toString}")
    }
  }
}
