import os
from time import sleep
from json import dumps, loads
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
            key_serializer=lambda x: bytes(x, "UTF-8"),
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
        data = loads(w.to_JSON())

        CACHE[city] = data

    except OWMError:
        data = CACHE[city]

    try:
        message = {
            "weather_main": data["status"],
            "weather_description": data["detailed_status"],
            "temp": data["temperature"]["temp"],
            "pressure": data["pressure"]["press"],
            "humidity": data["humidity"],
            "wind_speed": data["wind"]["speed"]
        }
        print('Sending: ', message)
        producer.send(TOPIC, value=message, key=city, partition=idx)
    except KafkaTimeoutError:
        print("Timeout")
        continue
    sleep(2)
