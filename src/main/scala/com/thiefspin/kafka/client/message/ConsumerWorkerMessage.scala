package com.thiefspin.kafka.client.message

trait ConsumerWorkerMessage

final case class ConsumeFromTopic() extends ConsumerWorkerMessage
