package com.akolar.maxaltitude;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;

import java.io.IOException;

public class MessageSchema implements DeserializationSchema<Message.AircraftBeacon>, SerializationSchema<Message.AircraftBeacon> {
        @Override
        public Message.AircraftBeacon deserialize(byte[] bytes) throws IOException {
            return Message.AircraftBeacon.parseFrom(bytes);
        }

        @Override
        public byte[] serialize(Message.AircraftBeacon beacon) {
            return beacon.toByteArray();
        }

        @Override
        public TypeInformation<Message.AircraftBeacon> getProducedType() {
            return TypeExtractor.getForClass(Message.AircraftBeacon.class);
        }

        // Method to decide whether the element signals the end of the stream.
        // If true is returned the element won't be emitted.
        @Override
        public boolean isEndOfStream(Message.AircraftBeacon myMessage) {
            return false;
        }
}
