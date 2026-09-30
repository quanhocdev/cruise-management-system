# Cruise Tour Management System

A microservice-based platform that manages the entire lifecycle of a cruise tour, from planning and ship assignment to passenger booking, onboard activity tracking, and final checkout. It covers everything that exists and happens on the ship: rooms, NFC wristbands, products, services, activities, ports, and billing.

---

## Table of Contents

- [Overview](#overview)
- [Roles](#roles)
- [Core Workflow](#core-workflow)
- [Business Rules](#business-rules)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

---

## Overview

The system serves two groups of users:

- **Passengers** browse tour packages, book rooms, pay online, and receive a QR code by email (Website and Android app).
- **Staff** manage ships, schedules, onboard offerings, check-in/check-out, and billing (Website and a mobile app that simulates a POS device).

Key capabilities:

- Ship management: floors, floor zones, rooms, and room types
- Tour planning with a draft, submit, review, and approve pipeline
- Assignment of ship areas to **products**, **services**, and **onboard activities**, plus configuration of **on-shore activities** at each port
- Tour package creation and online booking with capacity validation
- QR-code check-in, room assignment, and NFC wristband identification
- Per-passenger tracking of consumed products, services, onboard activities, and on-shore activities, with invoices settled at checkout
- Real-time notifications and feedback after each service or at the end of the trip

---

## Roles

| Role            | Responsibilities                                                                                                                                                                                                                                                                                        |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ADMIN**       | Manages private data and all accounts regardless of role. Manages ships (floors, zones, rooms, room types), NFC wristbands, product inventory, services, ports, and more.                                                                                                                               |
| **FINANCE**     | Front desk and finance. Checks in passengers by scanning the booking QR code, assigns rooms, issues an NFC wristband to each passenger, and checks out passengers to settle the final invoice at the end of the trip.                                                                                   |
| **SCHEDULER**   | Plans tours: the overall trip itinerary and the detailed schedule (daily plan and port stops, using ports created by ADMIN). Can only create and edit drafts. Once finished, submits the tour for review.                                                                                               |
| **OPERATION**   | Receives submitted tours, selects an available ship for the tour dates, chooses which categories the tour needs (and the ship supports), assigns ship areas to product, service, or activity, then approves to dispatch assignments. Later receives completed configurations and creates tour packages. |
| **ONBOARD**     | Pre-creates the catalog of **activities** that can be offered on the ship. When an assignment arrives, selects from the catalog and configures it, then marks it complete and returns it to OPERATION.                                                                                                  |
| **CONVENIENCE** | Pre-creates the catalog of **services** and **products** available on the ship. Handles assignments in the same way as ONBOARD.                                                                                                                                                                         |
| **SHORE**       | Manages **on-shore activities**. Not assigned a ship area by OPERATION, but still receives an assignment when a tour is approved. Configures it directly (nothing is pre-created). Their slots are the **daily port arrival times**, not on-ship locations.                                             |
| **PASSENGER**   | Browses tour packages, books rooms, pays, and receives a QR code by email. Leaves feedback after using a service or at the end of the trip.                                                                                                                                                             |

### Four Categories, Three Roles

The offerings on a tour fall into four categories, handled by exactly three roles:

| Category            | Handled by      |
| ------------------- | --------------- |
| Products            | **CONVENIENCE** |
| Services            | **CONVENIENCE** |
| Onboard activities  | **ONBOARD**     |
| On-shore activities | **SHORE**       |

These three roles also manage the data of passengers who have used or are using their offerings. Usage is recorded on the passenger's invoice for payment at checkout.

---

## Core Workflow

```
SCHEDULER ──(draft → submit)──▶ OPERATION ──(pick ship, assign areas/categories, approve)──▶
    ├─▶ ONBOARD      (activities)    ─┐
    ├─▶ CONVENIENCE  (services/products) ├─(mark complete)─▶ OPERATION ──▶ create tour packages ──▶ open registration
    └─▶ SHORE        (on-shore activities, port slots) ─┘

PASSENGER ──(select package, rooms, passengers)──▶ pay ──▶ QR code via email
    ──▶ FINANCE scans QR (check-in, room assignment, NFC wristband)
    ──▶ usage during trip recorded per passenger
    ──▶ FINANCE checkout and final payment
```

1. **Planning.** SCHEDULER creates a tour draft with its itinerary, daily schedule, and port stops, then submits it for review.
2. **Review and assignment.** OPERATION picks a ship that is free during the tour dates, selects the categories the tour requires that the ship can support, and assigns each area to one of three types: product, service, or activity. Approving dispatches the assignments to the other roles.
3. **Configuration.** ONBOARD (activities) and CONVENIENCE (products and services) choose from their pre-created catalogs and assign items to the areas. SHORE configures on-shore activities directly, using port-arrival slots. Each marks their work as complete, which sends it back to OPERATION.
4. **Packaging.** OPERATION creates tour packages from the completed configurations. Each package is tied to **one room type** of the ship, then opens registration.
5. **Booking.** PASSENGER selects a package and number of rooms, enters passenger details, and pays.
6. **Check-in.** After successful payment, a QR code is emailed. FINANCE scans it to check in each passenger, assign rooms, and attach an NFC wristband.
7. **During the trip.** Every product, service, onboard activity, and on-shore activity used is attached to the passenger's invoice.
8. **Checkout.** FINANCE checks out the passenger and collects the final invoice.
9. **Feedback.** Passengers can rate each service or the whole trip.

---

## Business Rules

- **Booking capacity:** the number of passengers in a booking must be **less than or equal to** the room type's maximum occupancy multiplied by the number of rooms selected.
- **Ship availability:** a ship can only be assigned to a tour if it is free during the tour dates.
- **Package and room type:** each tour package corresponds to exactly one room type.
- **Draft vs. approved:** SCHEDULER can only create and edit drafts. Final approval belongs to OPERATION.
- **Shore slots:** SHORE slots represent port arrival times per day, not on-ship locations.
- **Identification:** each checked-in passenger is linked to an NFC wristband for identification and billing on board.

---

## Architecture

The backend follows a **microservice architecture**.

| Service                  | Responsibility                                                                                                              |
| ------------------------ | --------------------------------------------------------------------------------------------------------------------------- |
| **auth-service**         | Registration, login, and session management.                                                                                |
| **tour-service**         | Core domain: ships, ports, schedules, assignments, catalogs, packages, NFC, inventory, onboard usage, and invoices.         |
| **booking-service**      | Passenger bookings and capacity validation.                                                                                 |
| **payment-service**      | Online payment and payment status handling.                                                                                 |
| **notification-service** | Email (QR code delivery) and real-time notifications.                                                                       |
| **feedback-service**     | Ratings and reviews after each service or at the end of the trip.                                                           |
| **common**               | Shared library: public geolocation API clients, Kafka events, Cloudinary file/image handling, and other reusable utilities. |

### Communication

- **Kafka:** asynchronous messaging between producers and consumers in different services, to avoid tight coupling.
- **REST over HTTP (service to service):** configured with timeouts and retries.
- **RESTful API:** internal handling within a service and client-facing endpoints.

### Security

- **JWT** authentication using Spring Boot's OAuth2 support (no custom filters).
- Role-based authorization for all roles listed above.

### Real-time

- **WebSocket:** notifications, booking updates, and more.
- **WebRTC:** internal communication through the browser over the LAN.

---

## Tech Stack

| Layer                  | Technology                   |
| ---------------------- | ---------------------------- |
| Backend                | Spring Boot                  |
| Web frontend           | React, JavaScript, HTML, CSS |
| Mobile                 | Android, Jetpack Compose     |
| Messaging              | Apache Kafka                 |
| Database               | PostgreSQL (with ORM)        |
| Cache / temporary data | Redis, Caffeine (e.g. OTP)   |
| Media storage          | Cloudinary (images, video)   |
| Real-time              | WebSocket, WebRTC            |
| Auth                   | JWT, Spring Security OAuth2  |
| Deployment             | Docker                       |

### Client Interfaces

- **Passenger:** Website and Android mobile app
- **Management:** Website and mobile app (simulating a POS device)

---

## Project Structure

```
.
├── backend/
│   ├── auth-service/
│   ├── tour-service/
│   ├── booking-service/
│   ├── payment-service/
│   ├── notification-service/
│   ├── feedback-service/
│   └── common/
├── frontend-web/          # React web app
├── frontend-android/      # Jetpack Compose Android app
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## Getting Started

### Prerequisites

- Java (JDK 17 or later recommended)
- Maven or Gradle
- Node.js and npm
- Docker and Docker Compose
- Android Studio (for the mobile app)

### 1. Clone the repository

```bash
git clone <your-repository-url>
cd <your-repository-folder>
cp .env.example .env   # then fill in your values
```

### 2. Start infrastructure

```bash
docker compose up -d
```

This starts the supporting services (PostgreSQL, Redis, Kafka, and so on). Adjust to match your `docker-compose.yml`.

### 3. Run the backend services

Run each service from its own directory:

```bash
cd backend/auth-service
./mvnw spring-boot:run
```

Repeat for `tour-service`, `booking-service`, `payment-service`, `notification-service`, and `feedback-service`.

### 4. Run the web app

```bash
cd frontend-web
npm install
npm start
```

### 5. Run the mobile app

Open the `frontend-android` folder in Android Studio, sync Gradle, and run it on an emulator or device.

---

## Configuration

Copy `.env.example` to `.env` and set the following variables (names are examples; adjust to your setup):

| Variable                                                               | Description                  |
| ---------------------------------------------------------------------- | ---------------------------- |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`                                 | PostgreSQL connection        |
| `REDIS_HOST`, `REDIS_PORT`                                             | Redis connection             |
| `KAFKA_BOOTSTRAP_SERVERS`                                              | Kafka brokers                |
| `JWT_ISSUER_URI` / `JWT_SECRET`                                        | JWT / OAuth2 configuration   |
| `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` | Cloudinary credentials       |
| `MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD`                          | Email delivery for QR codes  |
| `PAYMENT_*`                                                            | Payment provider credentials |

> Never commit secrets. Use a `.env` file or your secret manager and keep it out of version control.

---

## Screenshots

_Add screenshots or a demo link here._

---

## Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## License

_Specify your license here (for example, MIT)._

---

## Contact

_Add author name, email, or links here._
