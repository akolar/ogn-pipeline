package com.akolar.maxaltitude;

import com.rabbitmq.client.AMQP;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;

import java.io.IOException;

public class DataSource extends RMQSource {
    public DataSource(RMQConnectionConfig rmqConnectionConfig, String queueName, DeserializationSchema deserializationSchema) {
        super(rmqConnectionConfig, queueName, deserializationSchema);
    }

    @Override
    protected void setupQueue() throws IOException {
        channel.exchangeDeclare(Constants.Exchange,"fanout");
        AMQP.Queue.DeclareOk result = channel.queueDeclare(Constants.Queue, false, true, true, null);
        channel.queueBind(result.getQueue(), Constants.Exchange, "*");
    }
}
