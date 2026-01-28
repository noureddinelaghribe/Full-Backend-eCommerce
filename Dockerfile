FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and pom first (better build caching)
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Make mvnw executable (for Linux; on Windows git may lose the bit)
RUN chmod +x mvnw || echo "chmod not needed"

# Download dependencies (optional but improves caching)
RUN ./mvnw -B -q dependency:go-offline

# Copy source and build
COPY src src
RUN ./mvnw -B -q package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar from the build stage; update name if your final jar is different
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]

