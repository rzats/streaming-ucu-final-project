import os
from time import sleep
from json import loads
from kafka import KafkaConsumer
from kafka.errors import KafkaError


KAFKA_BROKERS = 'KAFKA_BROKERS'
TOPIC = 'test_topic_out'

broker_list = os.getenv(KAFKA_BROKERS)
connected = False
while not connected:
    try:
        consumer = KafkaConsumer(
            TOPIC,
            bootstrap_servers=broker_list,
            key_deserializer=lambda k: k.decode("UTF-8"),
            value_deserializer=lambda m: loads(m.decode("UTF-8"))
        )
        connected = True
        print('Connected!')
        sleep(5)
    except KafkaError:
        print("Retry")
        sleep(2)

print('\n'*10)
print("Started consuming!")
print('\n'*10)

for message in consumer:
    # message value and key are raw bytes -- decode if necessary!
    # e.g., for unicode: `message.value.decode('utf-8')`
    print("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                                 message.offset, message.key,
                                                 message.value))