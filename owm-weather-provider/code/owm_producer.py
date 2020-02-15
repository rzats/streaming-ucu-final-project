import os
from time import sleep
from json import dumps
from kafka import KafkaProducer
import pyowm
from pyowm.exceptions import OWMError
import random


MY_API_KEY = os.getenv('OWM_API_KEY', '88df1c42c33bad002f03400df389352e')
owm = pyowm.OWM(MY_API_KEY)

KAFKA_BROKERS = 'KAFKA_BROKERS'
TOPIC = 'weather_data'

broker_list = os.getenv(KAFKA_BROKERS).split(',')
producer = KafkaProducer(
    bootstrap_servers=broker_list,
    client_id='weather-provider'
    value_serializer=lambda x: dumps(x).encode('utf-8')
)

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

    producer.send(TOPIC, value=data)
    sleep(2)
