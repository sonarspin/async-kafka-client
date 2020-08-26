package com.thiefspin.kafka.client.actor

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import com.thiefspin.kafka.client.message.{ProduceToTopic, ProducerWorkerMessage}

class ProducerWorker(context: ActorContext[ProducerWorkerMessage]) extends DefaultBehavior(context) {

  override def onMessage(behavior: ProducerWorkerMessage): Behavior[ProducerWorkerMessage] = {
    behavior match {
      case ProduceToTopic(topic, msg, producer) => producer.produce(topic, msg)
    }
    Behaviors.same
  }
}

object ProducerWorker {

  val IDENTIFIER: String = "kafka_producer_worker"

  def apply[A](context: ActorContext[A]): ActorRef[ProducerWorkerMessage] = {
    val actor = context.spawn[ProducerWorkerMessage](
      Behaviors.supervise {
        Behaviors.setup[ProducerWorkerMessage] { ctx =>
          new ProducerWorker(ctx)
        }
      }.onFailure(SupervisorStrategy.resume), IDENTIFIER
    )
    context.watch(actor)
    actor
  }
}
