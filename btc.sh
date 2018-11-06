#!/bin/bash
USERNAME=ubuntu
HOSTS="ec2-13-59-236-183.us-east-2.compute.amazonaws.com"
SCRIPT="sudo mkdir /yeah"
for HOSTNAME in ${HOSTS} ; do
    ssh -i "dev_node.pem" ubuntu@${HOSTNAME} "${SCRIPT}"
done
