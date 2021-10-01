# Personal CRM Backend

Production environment IP address: 13.236.101.149

Development environment IP address: 3.104.226.94




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

### docker-compose-local-soft.sh

Running this will create a docker container for just the MySQL database, meaning that it is up to you to run the Spring Boot application. Do this if you have JDK 11 and the maven dependencies already installed on your local PC.

However, note that if you take this approach, you will need to set a few environment variables:

To set the required environment variables in IntelliJ:

1. Click on the Run tab

2. Click on Edit Configurations…

If you don’t see PersonalCRMApplication under Application, you need to run PersonalCRMApplication first. (It will throw an exception obviously)

4. Paste into “Environment variables”:
5. 
```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/personalCrmDB;MYSQL_ROOT_PASSWORD=admin;ENV=dev
```

5. Run the application and it should work, provided that you’ve already run docker-compose-local-soft.sh to startup the database server.



