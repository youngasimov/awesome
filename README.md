# awesome bank

Api to perform basic bank operations:
* create account
* add founds
* withdraw
* transfer between accounts
* get balance and transaction details

the API was built using [Play Framework](https://www.playframework.com/)

## Install
Before installation, you need to have:
* Java version 9 or superior
* [sbt](https://www.scala-sbt.org/)

1) `git clone https://github.com/youngasimov/awesome.git <folder-name>`
2) `cd <folder-name>`
3) `sbt test` execute the tests
4) `sbt run` execute the application on localhost:9000


## Usage

for the follow endpoints, the usage of `postman` is recommended for
manual testing

### Create account
`POST` /v1/accounts
```

{
    "name": "test-account",
    "initialDeposit": 10
}
```
`name` is the name of the account and must be unique. 
`initialDeposit` is not mandatory

### Get account list
`GET` /v1/accounts

### Get account details
`GET` /v1/accounts/:id

### Create transaction
`POST` /v1/transactions
```

{
    "senderId": 3,
    "receiverId": 5,
    "amount": 20
}
```
if only `senderId` is specified, the transactions is considered as withdraw
of money, if only `receiverId` is specified, the transaction is
considered as a deposit.