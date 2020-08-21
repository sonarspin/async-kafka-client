package com.thiefspin.kafka.client.actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import com.thiefspin.kafka.client.message.{Produce, ProduceToTopic, ProducerWorkerMessage}

class ProducerWorker(context: ActorContext[ProducerWorkerMessage]) extends AbstractBehavior[ProducerWorkerMessage](context) {

  override def onMessage(msg: ProducerWorkerMessage): Behavior[ProducerWorkerMessage] = {
    msg match {
      case ProduceToTopic(topic, msg, simpleKafkaProducer) => simpleKafkaProducer.produce(topic, msg)
    }
    Behaviors.same
  }
}

object ProducerWorker {

  val IDENTIFIER: String = "kafka_producer_worker"

  def apply[A](context: ActorContext[A]): ActorRef[ProducerWorkerMessage] = {
    context.spawn[ProducerWorkerMessage](
      Behaviors.setup(ctx => new ProducerWorker(ctx)),
      IDENTIFIER
    )
  }
}
