package com.akolar.maxaltitude;

import com.rabbitmq.client.AMQP;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.flink.util.Collector;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StreamingJob {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        final RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(Constants.Host)
                .setPort(Constants.Port)
                .setVirtualHost(Constants.VHost)
                .setUserName(Constants.Username)
                .setPassword(Constants.Password)
                .build();

        final DataStream<Message.AircraftBeacon> stream = env.addSource(
                new DataSource(connectionConfig, Constants.Queue, new MessageSchema()));

        KeyedStream<Message.AircraftBeacon, Tuple2<Integer, Integer>> latLonStream =
                stream.keyBy(new KeySelector<Message.AircraftBeacon, Tuple2<Integer, Integer>>() {
                    public Tuple2<Integer, Integer> getKey(Message.AircraftBeacon value) {
                        return new Tuple2<Integer, Integer>((int)value.getLatitude(), (int)value.getLongitude());
                    }
                });

        latLonStream
                .window(SlidingProcessingTimeWindows.of(Time.minutes(5), Time.seconds(30)))
                .aggregate(new Aggregator()).print();

        env.execute("Max Altitude");
    }

}
