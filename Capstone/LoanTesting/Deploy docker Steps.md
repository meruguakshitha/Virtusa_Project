Steps to perform docker :



backend: to create a Dockerfile

&nbsp;-->by giving this command to create war file mvn clean package -DskipTests

&nbsp; in filemanager open project path run cmd command is

&nbsp;         1)docker build -t loan-backend  :- Successfully tagged loan-    b ackend:latest

&nbsp;    2)docker run -p 8081:8081 loan-backend

&nbsp;   its starting running open http://localhost8081





-------------------------------------------------------------------------------------------------------------------------------------------------

.

&nbsp;BEFORE DEMO (DO THIS ONCE)

1️⃣ Make sure Docker Desktop is running



✔ Green “Docker is running” icon

✔ Linux engine enabled (you already have this)



2️⃣ Verify images exist

docker images





You should see:



loan-backend

mongo





If yes → you’re ready.

----------------------------------------------------------------------------------------------------------------------------------------------------

STEP 1: Start MongoDB

docker run -d --name mongo --network loan-network mongo:7





Explain:



“This starts MongoDB as a Docker container.”



🔹 STEP 2: Start Backend

docker run -d --name loan-backend-container --network loan-network -p 8081:8081 loan-backend





Explain:



“This starts the Spring Boot backend container and connects it to MongoDB.”



🔹 STEP 3: Show Running Containers

docker ps





Explain:



“Both backend and database are running independently in containers.”



🔹 STEP 4: Show Logs (OPTIONAL but impressive)

docker logs loan-backend-container





Explain:



“The backend is successfully connected to MongoDB.”



🔹 STEP 5: API Demo (MOST IMPORTANT)



Open Postman:



POST http://localhost:8081/api/auth/login





✔ Show JWT token

✔ Then show one secured API



🟢 AFTER DEMO (CLEANUP – OPTIONAL)

docker stop loan-backend-container mongo

-------------------------------------------------------------------------------------------------------------------------------

single command



--if mongo running somewhere to stop 

&nbsp;   >>docker rm -f mongo loan-backend-container

single command 

-->  >>docker compose up -d



 **Now Docker Compose will:**



Create its own Mongo container



>>Create backend container



Create network automatically



--->Docker stops

&nbsp;  >> docker compose down

