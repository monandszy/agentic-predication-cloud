PROJECT_NAME=agentic-prediction-cloud

cd ./../
./gradlew build -x test
cd docker

docker-compose -p data -f compose-data.yml up -d
docker-compose -p ${PROJECT_NAME} -f compose-prod.yml up -d --build