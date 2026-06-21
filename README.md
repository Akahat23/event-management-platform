# Event Management Platform

A full-stack web application designed to publish events, manage attendee records, handle secure seat registrations, and enforce real-time seat capacity limits.

The project uses a secure **Layered MVC (Model-View-Controller) Architecture** to connect a **Spring Boot REST API** backend with a modern **React (Vite)** frontend, secured with stateless **JSON Web Tokens (JWT)** and persisted in a **MySQL** database.

---

## 🛠️ Tech Stack

### Backend
*   **Java 17 / 25**
*   **Spring Boot 3.2.5**
*   **Spring Security & stateless JWT** (JSON Web Tokens)
*   **Spring Data JPA & Hibernate**
*   **MySQL Database** (HikariCP connection pool)

### Frontend
*   **React (Vite)** (Fast development & HMR)
*   **Axios** (With default headers & global response interceptors)
*   **TailwindCSS / Glassmorphism styling**
*   **Lucide React** (Modern UI icons)

---

## 🚀 Key Features

1.  **Event Management (CRUD)**: Authorized admins can create, view, update, and cancel events.
2.  **User Authentication**: Secure signup and login flow using BCrypt password hashing and JWT token issuance.
3.  **Real-Time Seat Booking**: Users can register for events. The system atomically checks seat availability, registers the attendee, and updates remaining capacity.
4.  **Seat Limit Lock**: Once an event hits its max capacity, the registration modal blocks bookings and shows a "Sold Out" alert.
5.  **Axios Token Interceptors**: Frontend automatically attaches the bearer token to all secure requests, and auto-logs out the user if the session expires (401/403 errors).
6.  **Admin Audit Logs**: Administrators have access to an Audit Dashboard listing all attendee profiles and registration logs.

---

## 📂 Project Directory Structure

```text
event-management-platform/
├── backend/                  # Spring Boot REST API
│   ├── src/main/java/com/event/platform/
│   │   ├── config/           # Startup initializers (Data seeding)
│   │   ├── controller/       # Rest Endpoints (Auth, Events, Registrations)
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── model/            # JPA Entities (User, Event, Attendee, Registration)
│   │   ├── repository/       # Data Access Layer (Spring Data JPA)
│   │   ├── security/         # JWT filter, Utils & SecurityFilterChain
│   │   └── service/          # Core Business Logic Services
│   └── pom.xml
│
└── frontend/                 # React UI (Vite)
    ├── src/
    │   ├── context/          # AuthState Context & Axios config
    │   ├── App.jsx           # Main routing, layout views & interceptors
    │   ├── index.css         # Styling system
    │   └── main.jsx          # Entry point wrapper
    ├── package.json
    └── vite.config.js