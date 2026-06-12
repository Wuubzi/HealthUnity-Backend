# 🏥 HealthUnity Backend

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring_AI-1.0.3-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=openai&logoColor=white)

**A production-ready RESTful backend for a medical appointment management platform, featuring an AI-powered virtual assistant, JWT-based OAuth2 security, and real-time email notifications.**

</div>

---

## 📖 Overview

HealthUnity Backend is the server-side core of a healthcare platform that connects **patients** and **doctors**. It handles appointment lifecycle management, doctor discovery, patient profiles, doctor dashboards, and an intelligent AI assistant called **EVA** — capable of managing medical appointments through natural language using MCP tools.

The system is designed with a clean layered architecture (Controller → Service → Repository), secured with OAuth2 JWT tokens, and containerized with Docker for deployment.

---

## ✨ Key Features

- **Full Appointment Lifecycle** — Create, update, cancel, and complete medical appointments with automated email confirmation
- **EVA AI Assistant** — Conversational AI powered by OpenAI GPT-4o-mini and Spring AI with MCP tool integration for appointment management
- **Doctor Discovery** — Paginated doctor search with filters by specialty, name, and relevance ranking
- **Doctor Dashboard** — Statistics, annual calendar, schedule management, and paginated appointment views
- **Patient Profiles** — Registration flow, profile completion, and profile updates
- **Favorites & Reviews** — Patients can save favorite doctors and leave star ratings with comments
- **JWT OAuth2 Security** — All endpoints (except `/public/**`) require a valid JWT token issued by an OAuth2 Authorization Server
- **HTML Email Notifications** — Styled appointment confirmation emails sent asynchronously via Gmail SMTP
- **Docker Ready** — Multi-stage Dockerfile using Gradle + Eclipse Temurin JDK 17

---

## 🛠️ Tech Stack

| Category | Technology | Purpose |
|---|---|---|
| **Language** | Java 17 | Core language |
| **Framework** | Spring Boot 3.5.7 | Application framework |
| **Security** | Spring Security + OAuth2 Resource Server | JWT authentication on all endpoints |
| **AI** | Spring AI 1.0.3 + OpenAI GPT-4o-mini | EVA virtual assistant |
| **AI Tools** | Spring AI MCP Client | Connects AI to real backend tools/actions |
| **Database** | PostgreSQL | Relational data storage |
| **ORM** | Spring Data JPA / Hibernate | Database access layer |
| **Email** | Spring Boot Mail (Gmail SMTP) | Async HTML appointment confirmation emails |
| **Validation** | Spring Boot Validation (Jakarta) | Request DTO validation |
| **WebSocket** | Spring WebSocket | Real-time communication support |
| **Build** | Gradle 8.10 | Build automation |
| **Containerization** | Docker (multi-stage build) | Deployment |
| **Utilities** | Lombok | Boilerplate reduction |

---

## 🤖 EVA — AI Medical Assistant

EVA is a conversational virtual assistant built into the platform. It uses **Spring AI** to connect to OpenAI's GPT-4o-mini model and is configured with a detailed system prompt that enforces:

- A warm, professional, plain-language tone (no Markdown in responses)
- Strict scope limited to HealthUnity ecosystem actions
- Explicit user confirmation before executing any action (schedule, cancel, or edit an appointment)
- Conversation memory maintained per user session (keyed by email)
- MCP Tool integration — EVA can invoke real backend tools to query and manage appointments on behalf of the user

**MCP (Model Context Protocol)** allows EVA to call structured tools (external MCP server) rather than hallucinating data. This bridges natural language understanding with actual database operations.

```
User: "Schedule an appointment with a cardiologist for next Monday at 10am"
EVA:  Confirms available doctors → asks for user confirmation → creates appointment → sends email
```

---

## 📁 Project Structure

```
src/main/java/com/healthUnity/backend/
├── Backend.java                    # Application entry point
├── Config/
│   └── SecurityConfig.java         # OAuth2 JWT security + CORS configuration
├── Controllers/
│   ├── CitasController.java         # Appointment endpoints
│   ├── DashboardController.java     # Doctor dashboard endpoints
│   ├── DoctorController.java        # Doctor discovery & social endpoints
│   ├── EspecialidadesController.java# Medical specialties endpoint
│   ├── EvaController.java           # AI assistant endpoint
│   └── PacienteController.java      # Patient registration & profile endpoints
├── Services/
│   ├── CitasService.java            # Appointment business logic + email trigger
│   ├── ChatService.java             # Spring AI chat client config for EVA
│   ├── DashboardService.java        # Dashboard stats, calendar, schedule logic
│   ├── DoctorService.java           # Doctor search, favorites, reviews
│   ├── EspecialidadesService.java   # Specialties business logic
│   ├── GmailService.java            # Async HTML email via Gmail SMTP
│   └── PacienteService.java         # Patient registration & profile logic
├── Models/                          # JPA Entities
│   ├── Citas.java                   # Appointment entity
│   ├── Doctores.java                # Doctor entity
│   ├── Paciente.java                # Patient entity
│   ├── DetallesUsuario.java         # Shared user details (name, email, phone, photo)
│   ├── Especialidades.java          # Medical specialty entity
│   ├── HorariosDoctor.java          # Doctor weekly schedule entity
│   ├── FavoritoDoctores.java        # Patient–Doctor favorites entity
│   ├── OpinionesDoctores.java       # Doctor reviews & star ratings entity
│   ├── Galeria.java                 # Doctor image gallery entity
│   └── Imagenes.java                # Individual image entity
├── Repositories/                    # Spring Data JPA interfaces
├── DTO/
│   ├── Request/                     # Incoming request DTOs
│   └── Response/                    # Outgoing response DTOs
├── Security/
│   └── CustomAuthenticationEntryPoint.java  # Custom 401 JSON error response
├── Exceptions/
│   └── GlobalExceptionHandler.java  # Centralized exception handling
└── Utils/
    └── DateFormatter.java           # Date formatting utility
```

---

## 🔌 API Endpoints

Base URL: `http://localhost:8000/api/v1`

> All endpoints require a valid Bearer JWT token unless noted.

### 👤 Patient — `/api/v1/paciente`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/register` | Register a new patient (checks if profile is complete) |
| `POST` | `/complete-profile` | Complete patient profile after OAuth2 signup |
| `GET` | `/getPaciente?gmail=` | Get patient details by email |
| `PUT` | `/update-profile` | Update patient profile information |

### 📅 Appointments — `/api/v1/citas`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/proxima-cita?idPaciente=` | Get the next upcoming appointment for a patient |
| `GET` | `/getCitas?idPaciente=&estado=` | List appointments by patient and status (`pendiente` / `cancelada` / `completada`) |
| `POST` | `/añadirCitas` | Create a new appointment + send confirmation email |
| `PUT` | `/editarCitas?idCita=` | Update an existing appointment |
| `PUT` | `/cancelarCitas?idCita=` | Cancel an appointment |
| `PUT` | `/completarCitas?idCita=` | Mark an appointment as completed |

### 🩺 Doctors — `/api/v1/doctor`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/top-doctores` | Get top-rated doctors |
| `GET` | `/getDoctores` | Paginated doctor list with search, specialty filter, and sort (`relevancia`) |
| `GET` | `/getDoctorById?idDoctor=` | Get full doctor profile by ID |
| `GET` | `/getReviewsByDoctorId?idDoctor=` | Get all reviews for a doctor |
| `GET` | `/getHorarioDoctor?idDoctor=` | Get doctor's weekly schedule with available slot count |
| `GET` | `/get-doctores-favoritos?idPaciente=` | Get a patient's favorite doctors |
| `POST` | `/añadir-favoritos` | Add a doctor to patient favorites |
| `POST` | `/añadir-opinion` | Submit a star rating and review for a doctor |
| `DELETE` | `/eliminar-favoritos?idFavorito=` | Remove a doctor from favorites |

### 🖥️ Doctor Dashboard — `/api/v1/dashboard`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/getDoctorProfile?gmailDoctor=` | Get doctor's profile info |
| `GET` | `/getStats?idDoctor=` | Get today's appointments, total patients, and 30-day attendance rate |
| `GET` | `/getProximasCitas?idDoctor=` | Get the next 4 upcoming appointments |
| `GET` | `/getCitas?idDoctor=&page=&size=&estado=` | Paginated appointments list with optional status filter |
| `GET` | `/getCalendarioCitas?idDoctor=` | Full annual calendar with appointments grouped by month and day |
| `GET` | `/getHorarioStats?idDoctor=` | Weekly hours, active days, and estimated weekly appointment capacity |
| `GET` | `/getHorarioDoctor?idDoctor=` | Doctor's weekly schedule with slot details |
| `POST` | `/saveHorarioDoctor` | Save or update the doctor's weekly schedule |

### 🏷️ Specialties — `/api/v1/especialidades`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/getEspecialidades` | List all available medical specialties |

### 🤖 EVA AI Assistant — `/api/v1/eva`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/ask` | Send a message to EVA; receives a natural-language response with optional tool-based actions |
| `DELETE` | `/conversation/{email}` | Clear conversation history for a user |

---

## ⚙️ Configuration

All sensitive values are injected via environment variables. Create a `.env` file or set the following in your environment:

```properties
# OpenAI
OPEN_AI_API_KEY=your_openai_api_key

# PostgreSQL
DATABASE_URL=jdbc:postgresql://host:port/dbname
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password

# OAuth2 JWT
ISSUER_URI=https://your-auth-server.com
JWT_AUDIENCIE=your_audience

# MCP Server (for EVA tools)
SERVER_URL=https://your-mcp-server-url

# Gmail SMTP
GMAIL_USER=your@gmail.com
GMAIL_PASSWORD=your_app_password
```

> The app exposes itself on port `8000`. CORS is pre-configured for `http://localhost:4200` (Angular dev) and the Vercel production frontend.

---

## 🚀 Running Locally

### With Gradle

```bash
# Clone the repository
git clone https://github.com/Wuubzi/HealthUnity-Backend.git
cd HealthUnity-Backend

# Set your environment variables, then run
./gradlew bootRun
```

### With Docker

```bash
# Build the image
docker build -t healthunity-backend .

# Run the container (pass your env vars)
docker run -p 8000:8000 \
  -e DATABASE_URL=... \
  -e DATABASE_USERNAME=... \
  -e DATABASE_PASSWORD=... \
  -e OPEN_AI_API_KEY=... \
  -e ISSUER_URI=... \
  -e JWT_AUDIENCIE=... \
  -e SERVER_URL=... \
  -e GMAIL_USER=... \
  -e GMAIL_PASSWORD=... \
  healthunity-backend
```

The Dockerfile uses a **multi-stage build**: Gradle 8.10 + JDK 17 to compile the JAR, then Eclipse Temurin JDK 17 (Jammy) as the lean runtime image.

---

## 🔐 Security

- All endpoints require a valid **Bearer JWT token** in the `Authorization` header.
- Tokens are validated against a configured OAuth2 Authorization Server (`issuer-uri` and `audiences`).
- Unauthorized requests return a structured JSON `401` error (not the default Spring redirect).
- CSRF is disabled (stateless JWT API).
- CORS is configured for specific allowed origins only.

---

## 📧 Email Notifications

When an appointment is created, a fully-styled **HTML confirmation email** is sent asynchronously to the patient's Gmail. The email includes:

- Doctor name, photo, and specialty
- Appointment date, time, and address
- Reason for the visit
- Reminder to arrive 15 minutes early
- Checklist of documents to bring

Emails are sent using Spring's `JavaMailSender` with Gmail SMTP + STARTTLS and are processed on a separate thread (`@Async`) to avoid blocking the HTTP response.

---
## 📄 License

This project is open source and available for portfolio and educational purposes.

---

<p align="center">
  Built with ❤️ by <a href="https://github.com/Wuubzi">Wuubzi</a>
</p>

