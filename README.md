# Hotel Backend - Taller Patron Facade

Backend en Spring Boot para un sistema de reservas de hotel. El estado se guarda en memoria (sin base de datos).

## Patrones de diseno

- **Facade**: `HotelFacade` ofrece una interfaz simple (`checkAvailability`, `createReservation`, `addService`, `checkIn`, `checkOut`, `findReservation`) y coordina los subsistemas `RoomService`, `RateService`, `AmenityService`, `BillingService` y `AccessService`. El controlador solo habla con la fachada.
- **Strategy**: `SeasonRateStrategy` con `HighSeasonRate` (x1.5 en diciembre, enero, junio y julio) y `LowSeasonRate` (x1.0 el resto del ano). `RateService` elige la estrategia segun el mes de check-in.
- **Factory**: `RoomFactory` crea las 15 habitaciones (6 SINGLE, 6 DOUBLE, 3 SUITE) en 3 pisos.
- **Builder**: `InvoiceBuilder` arma la factura final de forma fluida (`guestName(...)`, `addLine(...)`, `build()`).

## Tarifas

- Habitaciones por noche: SINGLE 80, DOUBLE 120, SUITE 250.
- Servicios extra: SPA 50 (unico), BREAKFAST 15 por noche, TRANSFER 30 (unico).

## Endpoints

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/hotel/availability?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD&type=SUITE` | Disponibilidad y precio total (`type` opcional) |
| POST | `/api/hotel/reserve` | Crea una reserva (201) |
| POST | `/api/hotel/services/{reservationId}` | Agrega un servicio: `{"serviceType":"SPA"}` |
| PUT | `/api/hotel/checkin/{reservationId}` | Check-in, genera la llave digital |
| PUT | `/api/hotel/checkout/{reservationId}` | Check-out, genera la factura y libera la habitacion |
| GET | `/api/hotel/reservation/{reservationId}` | Consulta una reserva |

Ejemplo de cuerpo para `/reserve`:

```json
{
  "guestName": "Ana Torres",
  "guestEmail": "ana@example.com",
  "guestPhone": "3001234567",
  "documentId": "1085123456",
  "roomNumber": 303,
  "checkIn": "2027-07-10",
  "checkOut": "2027-07-12"
}
```

Errores: `{ "message": "..." }` con 400 (datos invalidos), 404 (no existe) o 409 (conflicto de fechas o estado).

## Ejecutar

```bash
./mvnw spring-boot:run
./mvnw test
```

Variables de entorno: `PORT` (por defecto 8080) y `FRONTEND_URL` (origenes CORS separados por coma, por defecto `*`).
