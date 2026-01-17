FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/LoanPricingApprovalApplication-0.0.1-SNAPSHOT.war app.war

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.war"]
