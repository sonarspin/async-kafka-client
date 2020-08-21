package com.thiefspin.kafka.client.actor

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import com.thiefspin.kafka.client.message.{Consume, Produce, ProduceToTopic, ProducerWorkerMessage, WorkerSupervisorMessage}

class WorkerSupervisor(context: ActorContext[WorkerSupervisorMessage]) extends AbstractBehavior[WorkerSupervisorMessage](context) {

  private val producerWorker: ActorRef[ProducerWorkerMessage] = ProducerWorker(context)

  override def onMessage(msg: WorkerSupervisorMessage): Behavior[WorkerSupervisorMessage] = {
    msg match {
      case Produce(topic, msg, producer) => producerWorker ! ProduceToTopic(topic, msg, producer)
      case Consume() =>
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
