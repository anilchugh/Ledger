# WordCounter Application

## Build application
mvn clean install

## Start up application
mvn spring-boot:run

## Swagger Documentation
http://localhost:8080/swagger-ui/index.html

## Testing
There is data setup for basic entity(id=1) and account(id=1) with two wallets (id=1 and id=2)
Entity API can be tested via Swagger API to
* create a new entity

Account API can be tested via swagger to 
* create a new account 
* change state of account - freeze/unlock account or close account

Posting API can be tested via swagger to
* create a new posting

Wallet API can be tested via swagger to
* create a new wallet
* get wallets linked to account
* get balance for a given wallet


