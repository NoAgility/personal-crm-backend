#!/bin/bash
cd /home/ec2-user/personal-crm-backend
docker-compose build --no-cache
docker-compose up -d
