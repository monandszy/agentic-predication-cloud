PROJECT_NAME=agentic-prediction-cloud

docker-compose -p ${PROJECT_NAME} -f compose-prod.yml down
# docker-compose -p data -f compose-data.yml down