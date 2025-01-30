# Usa Ubuntu como base
FROM ubuntu:22.04

# Instala OpenJDK 21 y redis-tools
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk redis-tools netcat && \
    rm -rf /var/lib/apt/lists/*

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR de la aplicación y el script de espera
COPY target/challenge-0.0.1-SNAPSHOT.jar app.jar
COPY entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh

# Expone el puerto de la aplicación
EXPOSE 8080

# Usa el script de entrada
ENTRYPOINT ["./entrypoint.sh"]
