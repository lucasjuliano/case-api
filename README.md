# Setup

Add key inside the Dockerfile:

    ENV CASE_API_KEY = #INSERT HERE THE KEY

Building docker image:

    docker build -t case-api-clj .

Running docker:

     docker-compose up -d

# Usage
Access endpoint in
    
    http://localhost:3000

Endpoints available:
- /brokers

# Next steps

- Create rules for other endpoints
- Add a db instead of json, maybe datomic