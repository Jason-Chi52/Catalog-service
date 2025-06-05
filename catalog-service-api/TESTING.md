# Manual Testing Commands for Product API

## Create a Product

curl -X POST http://localhost:8080/products -H "Content-Type: application/json" -d "{\"name\":\"Chair\",\"description\":\"Wooden chair\",\"price\":79.99}"

## Get All Products

curl http://localhost:8080/products

## Get Product by ID

curl http://localhost:8080/products/1


## Update a Product

curl -X PUT http://localhost:8080/products/1 -H "Content-Type: application/json" -d "{\"name\":\"Chair Deluxe\",\"description\":\"Premium wood\",\"price\":99.99}"


## Delete a Product

curl -X DELETE http://localhost:8080/products/1
