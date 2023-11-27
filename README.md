# Weather API Application

The Weather API application is a simulation providing fake weather data.

## Usage

To run the "weather-api," execute the following command:

```bash
docker-compose up -d
```

After running the command, you can access the application at `http://localhost:8082`.

### Registration and Authentication

To use the application, you need to register first by making a POST request to `/api/register`. After registration, you can log in at `/api/login` to obtain a JwtToken. Use this token to request your personal apiKey at `/api/get-api-key`. With the apiKey, you can access the full power of the API, including endpoints like `/api/stations` and `/api/stations/{station_code}`. Remember to include your apiKey in the headers using `X-API-KEY`.

## Technology Stack

The application is built using the following technologies:

- **Spring WebFlux**
- **Spring Security** (Authorization via Jwt Token)
- **PostgreSQL**
- **Redis**
- **Rate Limiter**

Feel free to explore and experiment with the various endpoints provided by the API.

---

Feel free to adjust it further to better suit your preferences!
