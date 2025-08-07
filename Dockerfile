# Usa un'immagine Java 21
FROM eclipse-temurin:21-jdk-alpine

# Imposta la directory di lavoro
WORKDIR /app

# Copia i file necessari per Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Scarica le dipendenze (cache)
RUN ./mvnw dependency:go-offline

# Copia il codice sorgente
COPY src ./src

# Avvia l'applicazione Spring Boot
CMD ["./mvnw", "spring-boot:run"]