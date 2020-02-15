import os
from time import sleep
from json import dumps
from kafka import KafkaProducer
import pyowm
from pyowm.exceptions import OWMError
from kafka.errors import KafkaTimeoutError
import random


MY_API_KEY = os.getenv('OWM_API_KEY', '88df1c42c33bad002f03400df389352e')
owm = pyowm.OWM(MY_API_KEY)

KAFKA_BROKERS = 'KAFKA_BROKERS'
TOPIC = 'weather_data'

# sleep(30)
broker_list = os.getenv(KAFKA_BROKERS)
print('*'*30)
print(broker_list)
print('*'*30)
connected = False
while not connected:
    try:
        producer = KafkaProducer(
            bootstrap_servers=broker_list,
            client_id='weather-provider',
            value_serializer=lambda x: dumps(x).encode('utf-8')
        )
        connected = True
        print('Connected!')
        sleep(5)
    except Exception:
        print("Retry")
        sleep(2)


NUM_CITIES = 10
CITIES = ['London', 'Munich', 'Warsaw', 'Prague', 'Paris',
          'Brussels', 'Amsterdam', 'Madrid', 'Barcelona', 'Rome']

CACHE = {}
while True:
    idx = random.randint(0, NUM_CITIES-1)
    city = CITIES[idx]

    try:
        observation = owm.weather_at_place(city)
        w = observation.get_weather()
        data = {city: w.to_JSON()}

        CACHE[city] = data

    except OWMError:
        data = CACHE[city]

    try:
        print('Sending: ', data)
        producer.send(TOPIC, value=data, city)
    except KafkaTimeoutError:
        print("Timeout")
        continue
    sleep(2)
