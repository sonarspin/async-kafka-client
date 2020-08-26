package com.thiefspin.kafka.client.consumer

import com.thiefspin.kafka.client.json.JsonFormatter
import com.typesafe.scalalogging.LazyLogging

sealed trait ConsumerTransformer extends LazyLogging {
  def apply(msg: String, topic: String): Unit
}

final case class StringTransformer(f: String => Unit) extends ConsumerTransformer {

  override def apply(msg: String, topic: String): Unit = {
    f(msg)
  }
}

final case class TypedTransformer[Entity](f: Entity => Unit)(implicit formatter: JsonFormatter[Entity]) extends ConsumerTransformer {

  override def apply(msg: String, topic: String): Unit = {
    formatter.fromJsonString(msg) match {
      case Some(entity) => f(entity)
      case None => logger.error(s"Failed to parse kafka message from topic: $topic. Check formatter.")
    }
  }
}