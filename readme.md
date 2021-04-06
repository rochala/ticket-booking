# Ticket booking app

---

Ticket booking system build with akka-http.

1. [About](#about)
1. [Installing](#installing)
1. [API](#api)
1. [Dependencies](#dependencies)

---
## About
This system was build with akka-http high level API. I made following assumptions in this project:
* each row in screening room has same number of seats

Besides akka-http I used following libraries, technologies:
* *slick* for database connection,
* *circe* for json parsing,
* *posgresql* as database server,
* *scalatest* for unit testing.


## Installing

### Requirements
* Scala 2.13
* Java 1.8 or higher
* [Optional] PosgreSQL server

### Steps

1. **Clone this repository**
```git clone https://github.com/rochala/ticket-booking.git```
1. **Change directory to ticket-booking**
```cd ticket-booking```
1. **Checkout to development branch**
```git checkout development```
1. **Database configuration**
You can either use external database hosted on my server ( configuration is provided in email ) and skip next sections or
host it locally on your machine.
**Initializing postgreSQL on local machine**
1.1 Create new user:
```createuser $name -P --interactive```
1.1 Create new database:
```create database $databasename```
1.1 Connect to new database:
```\connect $newdatabase```
1.1 Run initializatin script:
```\i init.sql```
1.1 Grant privileges to created user:
```Grant usage, select, on all sequences in schema public to $user;
Grant all on all tables in schema public to $user;```

1. **Configure database connection in src/main/resources/application.conf file**
```nvim src/main/resources/application.conf```
1. **Start project**
```sbt run```

---

## API
API tests are provided in test_api.sh shell script.


## Dependencies
* Scala
* Akka, Akka-http
* Scalatest
* Circe
* Slick


