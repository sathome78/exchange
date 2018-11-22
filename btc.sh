#!/bin/bash
USERNAME=ubuntu
HOSTS="172.10.13.172"
SCRIPT="sudo mkdir /"
for HOSTNAME in ${HOSTS} ; do
    ssh -i "dev_node.pem" ubuntu@${HOSTNAME} "${SCRIPT}""$1"
done
