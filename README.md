# ogn-pipeline

OGN Pipeline is a proof-of-concept service that is streaming data from the
[Open Glider Network](https://glidernet.org) and aggregating altitudes inside every
(lat, lon) bucket and in 5 minute windows. Data is receiveved from the APRS
stream and then transformed into protobuf objects which are then sent to a
Pub/Sub system (implemented using RabbitMQ). A (streaming) Flink job then
aggregates received events and prints them to stdout.

- Stream consumer: [ogn-lib](https://github.com/akolar/ogn-lib)
- Pub/Sub system: [RabbitMQ](https://www.rabbitmq.com/) (messages are serialized
as [Protocol Buffers](https://developers.google.com/protocol-buffers/))
- Aggregator: [Apache Flink](https://flink.apache.org/)

## Usage

1. Deploy infrastructure using `docker-compuse up`
2. Deploy Flink job by running `./deploy.sh`
