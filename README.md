# Setup

Add key inside the Dockerfile:

    ENV CASE_API_KEY #INSERT HERE THE KEY

Building docker image:

    docker build -t case-api-clj .

Running docker:

     docker-compose up -d

# Usage
Access endpoint in
    
    http://localhost:3000

Endpoints available:
### /brokers

    json
    {
        "first_name": "f",
        "last_name": "l"
    }

### /brokers/:broker-id/quotes

    json
    {
        "sex":"m",
        "age":10
    }

### /brokers/:broker-id/policy

    json
    {
        "quotation_id": "uuid",
        "date_of_birth": "yyyy-mm-mm",
        "sex": "m",
        "name": "n" 
    }

### /brokers/:broker-id/policies/:policy-id

No json.
