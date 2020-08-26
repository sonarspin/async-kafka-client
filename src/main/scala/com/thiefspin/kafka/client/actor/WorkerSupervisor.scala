package com.thiefspin.kafka.client.actor

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import com.thiefspin.kafka.client.consumer.{ConsumerTransformer, KafkaConsumerType}
import com.thiefspin.kafka.client.message._

class WorkerSupervisor(context: ActorContext[WorkerSupervisorMessage]) extends DefaultBehavior(context) {

  private val producerWorker: ActorRef[ProducerWorkerMessage] = ProducerWorker(context)

  private def consumerWorker(topic: String, consumer: KafkaConsumerType, transformer: ConsumerTransformer): ActorRef[ConsumerWorkerMessage] = ConsumerWorker(topic, consumer, transformer, context)

  override def onMessage(msg: WorkerSupervisorMessage): Behavior[WorkerSupervisorMessage] = {
    msg match {
      case Produce(topic, msg, producer) =>
        context.log.info(s"Supervisor received message Produce($topic, $msg, $producer)")
        producerWorker ! ProduceToTopic(topic, msg, producer)
      case Consume(topic, consumer, transformer) =>
        consumerWorker(topic, consumer, transformer) ! ConsumeFromTopic()
    }
    Behaviors.same
  }

}

object WorkerSupervisor {

  val IDENTIFIER: String = "kafka_worker_supervisor"

  def apply[A](implicit system: ActorSystem[A]): ActorRef[WorkerSupervisorMessage] = {
    system.systemActorOf[WorkerSupervisorMessage](
      Behaviors.setup(ctx => new WorkerSupervisor(ctx)),
      IDENTIFIER
    )
  }

  def apply[A](implicit context: ActorContext[A]): ActorRef[WorkerSupervisorMessage] = {
    context.spawn[WorkerSupervisorMessage](
      Behaviors.setup(ctx => new WorkerSupervisor(ctx)),
      IDENTIFIER
    )
  }
}
