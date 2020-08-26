package com.thiefspin.kafka.client.actor

import akka.actor.typed._
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext}

abstract class DefaultBehavior[A](context: ActorContext[A]) extends AbstractBehavior[A](context) {

  override def onSignal: PartialFunction[Signal, Behavior[A]] = {
    case PostStop =>
      context.log.info(s"${context.self.path.name} stopped")
      this
    case PreRestart =>
      context.log.info(s"${context.self.path.name} restarted")
      this
    case Terminated(value) =>
      context.log.info(s"${context.self.path.name} terminated. $value")
      this
  }

}
