package com.akolar.maxaltitude;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple4;

class Aggregator implements AggregateFunction<Message.AircraftBeacon, Tuple4<Integer, Integer, Float, Long>, String> {
    @Override
    public Tuple4<Integer, Integer, Float, Long> createAccumulator() {
        return new Tuple4<>(0, 0, 0f, 0L);
    }

    @Override
    public Tuple4<Integer, Integer, Float, Long> add(Message.AircraftBeacon value, Tuple4<Integer, Integer, Float, Long> accumulator) {
        int lat = (int)value.getLatitude();
        int lon = (int)value.getLongitude();
        return new Tuple4<>(
                lat,
                lon,
                value.getAltitude() > accumulator.f2 ? value.getAltitude() : accumulator.f2,
                accumulator.f3 + 1
        );
    }

    @Override
    public String getResult(Tuple4<Integer, Integer, Float, Long> accumulator) {
        return String.format("(%d:%d): max=%f, count=%d", accumulator.f0, accumulator.f1, accumulator.f2, accumulator.f3);
    }

    @Override
    public Tuple4<Integer, Integer, Float, Long> merge(Tuple4<Integer, Integer, Float, Long> a, Tuple4<Integer, Integer, Float, Long> b) {
        return new Tuple4<>(
                a.f0, a.f1,
                a.f2 > b.f2 ? a.f2 : b.f2,
                a.f3 + b.f3
        );
    }
}
