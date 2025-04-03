# Fase de construcción
FROM eclipse-temurin:17.0.14_7-jdk AS build

# Establecer el directorio de trabajo en /app
WORKDIR /app

# Copiar el código fuente al contenedor
COPY . .

# Dar permisos de ejecución al script mvnw
RUN chmod +x mvnw

# Ejecutar Maven para compilar el proyecto y empaquetar el JAR (sin ejecutar los tests)
RUN ./mvnw clean package -DskipTests

# Fase de ejecución
FROM eclipse-temurin:17.0.14_7-jre

# Establecer el directorio de trabajo en /app
WORKDIR /app

# Copiar el archivo JAR generado en la fase de construcción al contenedor
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto 8080 en el contenedor para que la aplicación pueda ser accesible
EXPOSE 8080

# Configuración de entrada para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
