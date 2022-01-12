###
# Copyright 2017, Google, Inc.
# Licensed under the Apache License, Version 2.0 (the `License`);
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an `AS IS` BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
###

#!/usr/bin/python

import datetime
import time
import jwt
import paho.mqtt.client as mqtt
import json
from gpiozero import DistanceSensor
from gpiozero import LightSensor


# User variables

# Sensors
dist_sensor = DistanceSensor(23, 24)
light_sensor = LightSensor(18)

# Crypto
ssl_private_key_filepath = "/home/pi/Desktop/DT373B_Internet_of_Things_Systems_Design/lab5/private_key.pem"
ssl_algorithm = "RS256"  # Either RS256 or ES256
root_cert_filepath = "/home/pi/Desktop/DT373B_Internet_of_Things_Systems_Design/lab5/roots.pem"

# GCP
project_id = "triple-router-335423"
gcp_location = "europe-west1"
registry_id = "mattias_registry"
device_id = "mattias_rpi"

# end of user variables


def create_jwt():
    cur_time = datetime.datetime.utcnow()

    token = {
        "iat": cur_time,
        "exp": cur_time + datetime.timedelta(minutes = 60),
        "aud": project_id,
    }

    with open(ssl_private_key_filepath, "r") as f:
        private_key = f.read()

    return jwt.encode(token, private_key, ssl_algorithm)

def error_str(rc):
    return "{}: {}".format(rc, mqtt.error_string(rc))

def on_connect(unusued_client, unused_userdata, unused_flags, rc):
    print("on_connect", error_str(rc))

def on_publish(unused_client, unused_userdata, unused_mid):
    print("on_publish")


def main():
   
    client = mqtt.Client(
            client_id = 'projects/{}/locations/{}/registries/{}/devices/{}'.format(project_id, gcp_location, registry_id, device_id))

    # authorization is handled purely with JWT, no user/pass, so username can be whatever
    client.username_pw_set(username = 'unused', password = create_jwt())

    client.on_connect = on_connect
    client.on_publish = on_publish

    client.tls_set(ca_certs=root_cert_filepath)  
    client.connect('mqtt.googleapis.com', 8883)
    client.loop_start()

    sub_topic = 'events'
    mqtt_topic = '/devices/{}/{}'.format(device_id, sub_topic)


    for _ in range(1000):
        distance = round(dist_sensor.distance * 100,1)
        light = round(light_sensor.value, 2)
        lamp_on = True if distance < 40 and light < 0.5 else False

        payload = {'distance': distance, 'light': light, 'lamp_on': lamp_on}
        json_payload = json.dumps(payload)

        # Uncomment following line when ready to publish
        client.publish(mqtt_topic, json_payload, qos=1)

        # print('Payload: {}'.format(payload))
        print(payload)
        print(json_payload)
        print()

        time.sleep(0.1)


    client.loop_stop()


if __name__ == "__main__":
    main()
