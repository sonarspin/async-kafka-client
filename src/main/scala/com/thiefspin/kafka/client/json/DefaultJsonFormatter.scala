package com.thiefspin.kafka.client.json

import play.api.libs.json.{Format, Json}

final case class DefaultJsonFormatter[A]()(implicit format: Format[A]) extends JsonFormatter[A] {
  override def toJsonString(entity: A): String = {
    Json.toJson(entity).toString
  }
}
