package com.sonarspin.kafka.client.message

trait ConsumerWorkerMessage

final case class ConsumeFromTopic() extends ConsumerWorkerMessage
