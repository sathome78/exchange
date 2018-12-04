#!/bin/bash
USERNAME=ubuntu
HOSTS="$2"
SCRIPT="sudo mkdir "
for HOSTNAME in ${HOSTS} ; do
    ssh -i "dev_node.pem" ubuntu@${HOSTNAME} "${SCRIPT}""$1"
done
