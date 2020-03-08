import os
from time import sleep
from json import loads
from kafka import KafkaConsumer
from kafka.errors import KafkaError

import datetime

import dash
import dash_core_components as dcc
import dash_html_components as html
from dash.dependencies import Input, Output

KAFKA_BROKERS = 'KAFKA_BROKERS'
TOPIC = 'test_topic_out'

previous_msg = {
    'weather_main': 'foo'
}

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



external_stylesheets = ['https://codepen.io/chriddyp/pen/bWLwgP.css']

app = dash.Dash(__name__, external_stylesheets=external_stylesheets)
app.layout = html.Div(
    html.Div([
        html.H4('Weather'),
        html.Div(id='live-update-text'),
        dcc.Graph(id='live-update-graph'),
        dcc.Interval(
            id='interval-component',
            interval=5*1000, # in milliseconds
            n_intervals=0
        )
    ])
)




@app.callback(Output('live-update-text', 'children'),
              [Input('interval-component', 'n_intervals')])
def update_metrics(n):
    global previous_msg
    message = consumer.poll(timeout_ms=500)
    print("message:")
    print(message)
    style = {'padding': '5px', 'fontSize': '16px'}
    if not message:
        return [
            html.Span('Weather: {}'.format(previous_msg['weather_main']), style=style),
        ]

    # message value and key are raw bytes -- decode if necessary!
    # e.g., for unicode: `message.value.decode('utf-8')`
    print("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                         message.offset, message.key,
                                         message.value))

    previous_msg = message.value

    return [
        html.Span('Weather: {0:.2f}'.format(message.value['weather_main']), style=style),
    ]





# -*- coding: utf-8 -*-
# import dash
# import dash_core_components as dcc
# import dash_html_components as html
#
# external_stylesheets = ['https://codepen.io/chriddyp/pen/bWLwgP.css']
#
# app = dash.Dash(__name__, external_stylesheets=external_stylesheets)
#
# app.layout = html.Div(children=[
#     html.H1(children='Hello Dash'),
#
#     html.Div(children='''
#         Dash: A web application framework for Python.
#     '''),
#
#     dcc.Graph(
#         id='example-graph',
#         figure={
#             'data': [
#                 {'x': [1, 2, 3], 'y': [4, 1, 2], 'type': 'bar', 'name': 'SF'},
#                 {'x': [1, 2, 3], 'y': [2, 4, 5], 'type': 'bar', 'name': u'Montréal'},
#             ],
#             'layout': {
#                 'title': 'Dash Data Visualization'
#             }
#         }
#     )
# ])

if __name__ == '__main__':
    app.run_server(host='0.0.0.0')