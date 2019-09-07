import sys
import time

import pika
import yaml
from ogn_lib import OgnClient, Parser
from message_pb2 import AircraftBeacon


FIELDS_MAPPING = {
    'message_from': lambda x: x['from'],
    'destto': lambda x: x['destto'],
    'timestamp': lambda x: x['timestamp'].isoformat(),
    'latitude': lambda x: x['latitude'],
    'longitude': lambda x: x['longitude'],
    'altitude': lambda x: x['altitude'],
    'receiver': lambda x: x['receiver'],
    'uid': lambda x: x['uid'],
    'stealth': lambda x: x['stealth'],
    'do_not_track': lambda x: x['do_not_track'],
    'raw_message': lambda x: x['raw'],
    'relayer': lambda x: x['relayer'],
    'heading': lambda x: x['heading'],
    'ground_speed': lambda x: x['ground_speed'],
    'vertical_speed': lambda x: x['vertical_speed'],
    'turn_rate': lambda x: x['turn_rate'],
    'signal_to_noise_ratio': lambda x: x['signal_to_noise_ratio'],
    'error_count': lambda x: x['error_count'],
    'freq_offset': lambda x: x['frequency_offset'],
    'aircraft_type': lambda x: x['aircraft_type'].value,
    'address_type': lambda x: x['address_type'].value,
    'gps_quality.vertical': lambda x: x['gps_quality']['vertical'],
    'gps_quality.horizontal': lambda x: x['gps_quality']['horizontal'],
}


def callback(config, rabbit):
    def cb(data):
        bc = AircraftBeacon()
        for k, f in FIELDS_MAPPING.items():
            val = None

            try:
                val = f(data)
            except KeyError:
                pass

            if val is None:
                continue

            root = bc
            path = k.split('.')
            for p in path[:-1]:
                root = getattr(root, p)

            setattr(root, path[-1], val)

        rabbit.basic_publish(exchange=config['exchange'],
                             routing_key='',
                             body=bc.SerializeToString())

    return cb


def load_config():
    with open('./config.yml', 'r') as f:
        return yaml.load(f.read(), Loader=yaml.FullLoader)


def config_rabbit(config):
    exception = None
    for i in range(5):
        try:
            connection = pika.BlockingConnection(pika.ConnectionParameters('rabbit'))
            channel = connection.channel()
            channel.exchange_declare(exchange=config['exchange'], exchange_type='fanout')
            return channel
        except Exception as e:
            print('failed to create rabbit exchange: {}'.format(e), file=sys.stderr)
            exception = e
            time.sleep(2 ** i)

    raise exception


def main():
    config = load_config()
    rabbit = config_rabbit(config)

    client = OgnClient('N0CALL')
    client.connect()
    client.receive(callback(config, rabbit), parser=Parser)


if __name__ == '__main__':
    main()
