package com.sonarspin.kafka.client.consumer.impl

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import com.sonarspin.kafka.client.consumer.{ConsumerTransformer, KafkaConsumerType}
import com.sonarspin.kafka.client.message.{ConsumeFromTopic, ConsumerWorkerMessage}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success, Try}

class AkkaStreamKafkaConsumer(settings: ConsumerSettings[String, String])(implicit system: ActorSystem[_]) extends LazyLogging {

  private implicit val executionContext: ExecutionContextExecutor = system.executionContext

  implicit lazy val consumer = new KafkaConsumerType {
    override def consume[Entity](topic: String, transformer: ConsumerTransformer, actorRef: ActorRef[ConsumerWorkerMessage]): Unit = {
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
        }.onComplete {
        case Success(_) => actorRef ! ConsumeFromTopic()
        case Failure(ex) =>
          logger.error(s"Kafka worker ${actorRef.path.name} failed with exception: ${ex.getMessage}. Restarting consumer.")
          actorRef ! ConsumeFromTopic()
      }
    }
  }

}

object AkkaStreamKafkaConsumer {

  def apply[A](settings: ConsumerSettings[String, String])(implicit system: ActorSystem[A]): AkkaStreamKafkaConsumer = {
    new AkkaStreamKafkaConsumer(settings)
  }
}
