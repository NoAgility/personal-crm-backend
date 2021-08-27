bash purge.sh
docker-compose --env-file ./config/dev.env build --no-cache
docker-compose --env-file ./config/dev.env up -d