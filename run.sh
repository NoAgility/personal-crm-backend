#!/bin/bash
cd /home/ec2-user/personal-crm-backend
docker-compose --file docker-compose-springboot.yml --env-file config/dev.env up -d
