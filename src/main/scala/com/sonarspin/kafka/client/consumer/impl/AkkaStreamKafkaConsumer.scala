package com.sonarspin.kafka.client.consumer.impl

import akka.actor.typed.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import com.sonarspin.kafka.client.consumer.{ConsumerTransformer, KafkaConsumerType}
import com.thiefspin.kafka.client.consumer.ConsumerTransformer
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success, Try}

class AkkaStreamKafkaConsumer(settings: ConsumerSettings[String, String])(implicit system: ActorSystem[_]) extends LazyLogging {

  implicit lazy val consumer = new KafkaConsumerType {
    override def consume[Entity](topic: String, transformer: ConsumerTransformer): Unit = {
      akka.kafka.scaladsl.Consumer
        .committableSource(settings, Subscriptions.topics(topic))
        .runWith {
          Sink.foreach { msg =>
            Try {
              transformer(msg.record.value, topic)
            } match {
              case Failure(exception) =>
                logger.error(
                  s"Kafka Consumer failed to apply function to $topic." +
                    s"Reason $exception"
                )
              case Success(_) => () //Do Nothing
            }
          }
        }
    }
  }

}

object AkkaStreamKafkaConsumer {

  def apply[A](settings: ConsumerSettings[String, String])(implicit system: ActorSystem[A]): AkkaStreamKafkaConsumer = {
    new AkkaStreamKafkaConsumer(settings)
  }
}
