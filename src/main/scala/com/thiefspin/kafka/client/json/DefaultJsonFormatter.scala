package com.thiefspin.kafka.client.json

import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.{Format, JsError, JsSuccess, Json}

final case class DefaultJsonFormatter[A]()(implicit format: Format[A]) extends JsonFormatter[A] with LazyLogging {
  override def toJsonString(entity: A): String = {
    Json.toJson(entity).toString
  }

  override def fromJsonString(json: String): Option[A] = {
    Json.parse(json).validate[A] match {
      case JsSuccess(value, _) => Option(value)
      case JsError(errors) => logger.error(s"Failed to parse json string: " +
        s"$json. " +
        "Reason:" +
        s"${errors.mkString("\n")}")
        None
    }
  }
}
