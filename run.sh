#!/bin/bash
cd /home/ec2-user/personal-crm-backend
docker-compose build --no-cache
docker-compose --env-file /home/ec2-user/personal-crm-backend/config/prod.env up -d
