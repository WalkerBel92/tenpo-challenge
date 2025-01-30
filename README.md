# Challenge API

## Descripción del proyecto

Este proyecto es un desafío para un ingeniero de software backend. Consiste en un servicio web desarrollado con Java 21, Spring Boot 3.4.2 y Spring WebFlux, que incluye:

* Limitación de Tasa de Solicitudes: Máximo de 3 peticiones por minuto. La cuarta petición lanza una excepción controlada con Status Code 429.
* Manejo Global de Excepciones.
* Registro de Llamadas: Un filtro captura las llamadas a los endpoints y las guarda en una base de datos PostgreSQL para generar un CallLog. Si se obtiene Status Code 429 más de una vez en el minuto, solo se guarda el primer intento para asegurar la performance.

## Instrucciones para ejecutar el servicio localmente

1. Clona el repositorio:
    ```bash
    git clone <URL_DEL_REPOSITORIO>
    cd challenge
    ```

2. Asegúrate de tener `Java 21`, `Maven` y `Docker` instalados en tu máquina.

3. Compila y ejecuta el proyecto:
    ```bash
    mvn clean install
    ```

4. Ve a la raíz del proyecto y ejecuta `Docker Compose` para iniciar `PostgreSQL` y `Redis`:
    ```bash
    docker-compose up -d
    ```

5. Crea la base de datos llamada `challenge` en PostgreSQL. Puedes hacerlo utilizando algún gestor de base de datos de tu preferencia o con el siguiente comando:
    ```bash
    docker exec -it <nombre_del_contenedor_postgres> psql -U <usuario> -c "CREATE DATABASE challenge;"
    ```

6. Ejecuta el proyecto:
    ```bash
    mvn spring-boot:run
    ```

7. Descarga la Postman Collection: Puedes descargar la colección de `Postman` ubicada en la carpeta **`resources/postman`** para probar cada servicio. Ubícala con el nombre `Tenpo - Challenge.postman_collection.json`.

## Instrucciones para consumir los endpoints

### Endpoint 1: Obtener cálculo de dos números.

- **URL**: `/calculation`
- **Método HTTP**: `GET`
- **Descripción**: Obtener cálculo de dos números enviados por QueryParam y añadirle un porcentaje obtenido desde un servicio externo.
- **Parámetros**:
- `number1` (number): Primer número.
- `number2` (number): Segundo número.
- **Ejemplo de solicitud**:
    ```bash
    curl -X GET "http://localhost:8080/calculation/?number1=12&number2=50" -H "accept: application/json"
    ```
- **Ejemplo de respuesta**:
    ```json
    68.2
    ```

### Endpoint 2: Obtiene el listado de logs de llamadas.

- **URL**: `/call-logs`
- **Método HTTP**: `GET`
- **Descripción**: Este endpoint lista el log de llamadas de forma paginada, 10 registros por pagina y con opción de modificar estos parámetros.
- **Parámetros opcionales**:
   - `page` (number): Página solicitada. Por defecto en 0.
   - `size` (number): Cantidad de registros por página. Por defecto en 10.
- **Ejemplo de solicitud**:
    ```bash
    curl -X POST "http://localhost:8080/api/endpoint2" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"param1\":\"value1\",\"param2\":\"value2\"}"
    ```
  - **Ejemplo de respuesta**:
      ```json
      [
        {
          "id": 271,
          "timestamp": "2025-01-29 14:28:52.050",
          "endpoint": "/call-logs",
          "parameters": "queryParams={}, pathParams=/call-logs, body=",
          "response": null,
          "error": "{\"message\":\"Error en el servidor\",\"details\":\"404 NOT_FOUND \\\"No static resource call-logs.\\\"\",\"timestamp\":\"2025-01-29 14:28:52\",\"path\":\"/call-logs\"}",
          "statusCode": 500
        },
        {
          "id": 270,
          "timestamp": "2025-01-29 14:25:00.751",
          "endpoint": "/calculation/",
          "parameters": "queryParams={number1=[12], number2=[50]}, pathParams=/calculation/, body=",
          "response": "68.2",
          "error": null,
          "statusCode": 200
        },
        {
          "id": 269,
          "timestamp": "2025-01-29 13:49:42.401",
          "endpoint": "/swagger-ui.html",
          "parameters": "queryParams={}, pathParams=/swagger-ui.html, body=",
          "response": null,
          "error": "{\"message\":\"Error en el servidor\",\"details\":\"404 NOT_FOUND \\\"No static resource swagger-ui.html.\\\"\",\"timestamp\":\"2025-01-29 13:49:42\",\"path\":\"/swagger-ui.html\"}",
          "statusCode": 500
        },
        {
          "id": 268,
          "timestamp": "2025-01-29 13:35:07.022",
          "endpoint": "/calculation/",
          "parameters": "queryParams={number1=[12], number2=[50]}, pathParams=/calculation/, body=",
          "response": null,
          "error": "{\"message\":\"Límite de solicitudes excedido\",\"details\":\"Rate limit exceeded\",\"timestamp\":\"2025-01-29 13:35:07\",\"path\":\"/calculation/\"}",
          "statusCode": 429
        },
        {
          "id": 267,
          "timestamp": "2025-01-29 13:35:06.338",
          "endpoint": "/calculation/",
          "parameters": "queryParams={number1=[12], number2=[50]}, pathParams=/calculation/, body=",
          "response": "68.2",
          "error": null,
          "statusCode": 200
        },
        {
          "id": 266,
          "timestamp": "2025-01-29 13:35:05.627",
          "endpoint": "/calculation/",
          "parameters": "queryParams={number1=[12], number2=[50]}, pathParams=/calculation/, body=",
          "response": "68.2",
          "error": null,
          "statusCode": 200
        },
        {
          "id": 265,
          "timestamp": "2025-01-29 13:35:04.730",
          "endpoint": "/calculation/",
          "parameters": "queryParams={number1=[12], number2=[50]}, pathParams=/calculation/, body=",
          "response": "68.2",
          "error": null,
          "statusCode": 200
        },
        {
          "id": 264,
          "timestamp": "2025-01-29 13:26:53.584",
          "endpoint": "/swagger-ui.html",
          "parameters": "queryParams={}, pathParams=/swagger-ui.html, body=",
          "response": null,
          "error": "404 NOT_FOUND \"No static resource swagger-ui.html.\"",
          "statusCode": 200
        },
        {
          "id": 263,
          "timestamp": "2025-01-29 13:26:50.585",
          "endpoint": "/v3/api-docs",
          "parameters": "queryParams={}, pathParams=/v3/api-docs, body=",
          "response": "{\"openapi\":\"3.1.0\",\"info\":{\"title\":\"OpenAPI definition\",\"version\":\"v0\"},\"servers\":[{\"url\":\"http://localhost:8080\",\"description\":\"Generated server url\"}],\"paths\":{\"/call-logs\":{\"get\":{\"tags\":[\"call-log-controller\"],\"operationId\":\"getCallLogs\",\"parameters\":[{\"name\":\"page\",\"in\":\"query\",\"required\":false,\"schema\":{\"type\":\"integer\",\"format\":\"int32\",\"default\":0}},{\"name\":\"size\",\"in\":\"query\",\"required\":false,\"schema\":{\"type\":\"integer\",\"format\":\"int32\",\"default\":10}}],\"responses\":{\"200\":{\"description\":\"OK\",\"content\":{\"*/*\":{\"schema\":{\"type\":\"array\",\"items\":{\"$ref\":\"#/components/schemas/CallLog\"}}}}}}}},\"/calculation/\":{\"get\":{\"tags\":[\"calculation-controller\"],\"operationId\":\"calculate\",\"parameters\":[{\"name\":\"number1\",\"in\":\"query\",\"required\":true,\"schema\":{\"type\":\"number\",\"format\":\"double\"}},{\"name\":\"number2\",\"in\":\"query\",\"required\":true,\"schema\":{\"type\":\"number\",\"format\":\"double\"}}],\"responses\":{\"200\":{\"description\":\"OK\",\"content\":{\"*/*\":{\"schema\":{\"type\":\"number\",\"format\":\"double\"}}}}}}}},\"components\":{\"schemas\":{\"CallLog\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"timestamp\":{\"type\":\"string\",\"format\":\"date-time\"},\"endpoint\":{\"type\":\"string\"},\"parameters\":{\"type\":\"string\"},\"response\":{\"type\":\"string\"},\"error\":{\"type\":\"string\"},\"statusCode\":{\"type\":\"integer\",\"format\":\"int32\"}}}}}}",
          "error": null,
          "statusCode": 200
        },
        {
          "id": 262,
          "timestamp": "2025-01-29 13:26:48.646",
          "endpoint": "/swagger-ui.html",
          "parameters": "queryParams={}, pathParams=/swagger-ui.html, body=",
          "response": null,
          "error": "404 NOT_FOUND \"No static resource swagger-ui.html.\"",
          "statusCode": 200
        }
    ]

## Ejemplos de errores comunes

### Error 1: Límite de solicitudes excedido

- **Código de estado**: `429 Too Many Requests`
- **Descripción**: Este error ocurre cuando se excede el límite de solicitudes permitidas.
- **Ejemplo de respuesta**:
    ```json
    {
      "message": "Límite de solicitudes excedido",
      "details": "Rate limit exceeded",
      "timestamp": "2025-01-29 14:31:48",
      "path": "/calculation/"
   }
    ```

### Error 2: Parámetros faltantes

- **Código de estado**: `400 Bad Request`
- **Descripción**: Este error ocurre cuando faltan parámetros requeridos en la solicitud.
- **Ejemplo de respuesta**:
    ```json
    {
      "message": "Faltan parámetros",
      "details": "Required query parameter 'number1' is not present.",
      "timestamp": "2025-01-29 14:33:34",
      "path": "/calculation/"
   }
    ```
  
### Error 3: Tipo de parámetros incorrectos

- **Código de estado**: `400 Bad Request`
- **Descripción**: Este error ocurre cuando algún parámetro en la solicitud se envía con un tipo de dato incorrecto.
- **Ejemplo de respuesta**:
    ```json
    {
      "message": "Tipo de parámetro inválido",
      "details": "Type mismatch.",
      "timestamp": "2025-01-29 14:37:25",
      "path": "/calculation/"
    }
    ```

### Error 4: Recurso no encontrado

- **Código de estado**: `502 Bad Gateway`
- **Descripción**: Este error ocurre cuando el recurso solicitado no se encuentra.
- **Ejemplo de respuesta**:
    ```json
    {
      "message": "Reintentos agotados al servicio externo",
      "details": "Failed to fetch percentage after 3 retries",
      "timestamp": "2025-01-29 14:35:46",
      "path": "/calculation/"
   }
    ```

### Error 5: Error en el servidor

- **Código de estado**: `500 Internal Server Error`
- **Descripción**: Este error ocurre cuando se detecta un error en el servidor.
- **Ejemplo de respuesta**:
    ```json
    {
      "message": "Error en el servidor",
      "details": "Internal server error",
      "timestamp": "2025-01-29 14:35:46",
      "path": "/calculation/"
   }
    ```

## Notas adicionales

- Asegúrate de que los puertos 8080 (para el servicio) y 6379 (para Redis) estén disponibles y no en uso por otros servicios.
- Si encuentras problemas, revisa los logs en la consola para obtener más detalles sobre posibles errores.