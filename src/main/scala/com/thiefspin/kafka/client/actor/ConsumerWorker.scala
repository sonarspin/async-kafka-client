package com.thiefspin.kafka.client.actor

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import com.thiefspin.kafka.client.consumer.{ConsumerTransformer, KafkaConsumerType}
import com.thiefspin.kafka.client.message.{ConsumeFromTopic, ConsumerWorkerMessage}

class ConsumerWorker(topic: String, consumer: KafkaConsumerType, transformer: ConsumerTransformer, context: ActorContext[ConsumerWorkerMessage]) extends DefaultBehavior(context) {

  override def onMessage(behavior: ConsumerWorkerMessage): Behavior[ConsumerWorkerMessage] = {
    behavior match {
      case ConsumeFromTopic() => consumer.consume(topic, transformer)
    }
    Behaviors.same
  }
}

object ConsumerWorker {

  def apply[A](topic: String, consumer: KafkaConsumerType, transformer: ConsumerTransformer, context: ActorContext[A]): ActorRef[ConsumerWorkerMessage] = {
    val actor = context.spawn[ConsumerWorkerMessage](
      Behaviors.supervise {
        Behaviors.setup[ConsumerWorkerMessage] { ctx =>
          new ConsumerWorker(topic, consumer, transformer, ctx)
        }
      }.onFailure(SupervisorStrategy.resume), s"kafka_consumer_worker_$topic"
    )
    context.watch(actor)
    actor
  }
}


