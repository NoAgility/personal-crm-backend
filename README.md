# Personal CRM Backend

Production environment IP address: https://api.app.noagility-personal-crm.com

Development environment IP address: https://api.dev.noagility-personal-crm.com



**There are multiple ways of running the Spring Boot application, three of which are listed below:**

### The slow way - docker-compose-local.sh

Running this will create docker containers for the Spring Boot application and MySQL database.
Note that docker downloads all the dependencies whether or not you have it on your local PC already, because of this, this process can take up to 10 minutes.

### Run Spring Boot app without docker

It’s possible now to run the Spring Boot app without docker or a MySQL server thanks to the addition of the h2 in-memory database.

The profiles ‘local’ and ‘test’ uses the h2 in-memory database which is automatically configured and set up by Spring Boot.

To run the application with the in-memory database, put into environment variables:

```
ENV=local
```

Then compile as a maven project and run the Spring Boot application.

