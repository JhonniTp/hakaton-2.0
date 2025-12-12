# Sistema de Gestión de Hackatones

Sistema web completo para la gestión y administración de eventos de hackathon, desarrollado con Spring Boot y MySQL.

## 📋 Descripción

Este proyecto es una plataforma integral que permite organizar, administrar y evaluar hackatones. El sistema proporciona diferentes interfaces según el rol del usuario (Administrador, Participante o Jurado) y facilita todo el proceso desde el registro hasta la evaluación de proyectos.

## 🚀 Características Principales

### Para Administradores
- **Gestión de Hackatones**: Crear, editar y administrar eventos de hackathon
- **Gestión de Usuarios**: CRUD completo de usuarios con asignación de roles
- **Gestión de Equipos**: Administración de equipos participantes
- **Gestión de Categorías**: Organización de hackatones por categorías
- **Panel de Control**: Dashboard con estadísticas y métricas del sistema
- **Acciones en Lote**: Operaciones masivas sobre usuarios (cambio de rol, eliminación)
- **Exportación de Datos**: Exportar información a Excel/CSV

### Para Participantes
- **Registro e Inscripción**: Registro en la plataforma y hackatones
- **Gestión de Equipos**: Crear y unirse a equipos
- **Envío de Proyectos**: Subir proyectos con descripción y entregables
- **Dashboard Personal**: Vista personalizada con información relevante
- **Perfil de Usuario**: Gestión de información personal y experiencia

### Para Jurados
- **Evaluación de Proyectos**: Calificar proyectos según criterios establecidos
- **Sistema de Puntuación**: Puntuación de 0.0 a 5.0 con feedback detallado
- **Dashboard de Evaluación**: Vista especializada para revisión de proyectos
- **Criterios de Evaluación**: Evaluación basada en múltiples criterios

## 🛠️ Tecnologías Utilizadas

### Backend
- **Spring Boot 3.5.5**
- **Java 17**
- **Spring Data JPA** - Persistencia de datos
- **Spring Security** - Autenticación y autorización
- **Spring Validation** - Validación de datos
- **Lombok** - Reducción de código boilerplate
- **MySQL Connector** - Conexión con base de datos

### Frontend
- **Thymeleaf** - Motor de plantillas
- **HTML5/CSS3** - Estructura y estilos
- **JavaScript** - Funcionalidad interactiva
- **Bootstrap** (implícito en las vistas)

### Base de Datos
- **MySQL 8.0+**

### Autenticación
- **Spring Security**
- **Google OAuth 2.0** - Login con Google

## 📦 Requisitos Previos

Antes de ejecutar este proyecto, asegúrate de tener instalado:

- **Java JDK 17** o superior
- **Maven 3.6+**
- **MySQL 8.0+**
- **Git**

## 🔧 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/JhonniTp/hakaton-2.0.git
cd hakaton-2.0
```

### 2. Configurar la Base de Datos

Crear una base de datos MySQL:

```sql
CREATE DATABASE dbhackaton CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar Aplicación

Editar el archivo `src/main/resources/application.properties` con tus credenciales:

```properties
# Configuración de Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/dbhackaton?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# Puerto del servidor (opcional, por defecto 8083)
server.port=8083
```

### 4. Compilar el Proyecto

```bash
./mvnw clean install
```

O en Windows:

```bash
mvnw.cmd clean install
```

### 5. Ejecutar la Aplicación

```bash
./mvnw spring-boot:run
```

O en Windows:

```bash
mvnw.cmd spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8083`

## 📁 Estructura del Proyecto

```
hakaton-2.0/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hakaton/hakaton/
│   │   │       ├── config/           # Configuraciones (Security, etc.)
│   │   │       ├── controller/       # Controladores REST y MVC
│   │   │       ├── dto/              # Data Transfer Objects
│   │   │       ├── model/            # Entidades JPA
│   │   │       ├── repository/       # Repositorios JPA
│   │   │       ├── service/          # Lógica de negocio
│   │   │       │   └── impl/         # Implementaciones de servicios
│   │   │       └── HakatonApplication.java
│   │   └── resources/
│   │       ├── static/              # Recursos estáticos
│   │       │   ├── css/             # Hojas de estilo
│   │       │   ├── js/              # JavaScript
│   │       │   └── img/             # Imágenes
│   │       ├── templates/           # Plantillas Thymeleaf
│   │       │   ├── admin/           # Vistas de administrador
│   │       │   ├── jurado/          # Vistas de jurado
│   │       │   ├── participante/    # Vistas de participante
│   │       │   └── login.html       # Vista de login
│   │       └── application.properties
│   └── test/                        # Tests unitarios e integración
├── pom.xml                          # Configuración Maven
└── README.md
```

## 👥 Roles de Usuario

El sistema maneja tres roles principales:

### 1. **ADMINISTRADOR**
- Acceso completo al sistema
- Gestión de todos los recursos
- Dashboard administrativo
- Acceso: `/admin/**`

### 2. **PARTICIPANTE**
- Registro en hackatones
- Gestión de equipos y proyectos
- Dashboard de participante
- Acceso: `/participante/**`

### 3. **JURADO**
- Evaluación de proyectos
- Dashboard de evaluación
- Acceso: `/jurado/**`

## 🗃️ Modelo de Datos

### Entidades Principales

- **HackatonModel**: Eventos de hackathon con fechas, categorías y límites de participantes
- **UsuarioModel**: Usuarios del sistema con roles y autenticación
- **EquipoModel**: Equipos participantes en hackatones
- **ProyectoModel**: Proyectos presentados por equipos
- **EvaluacionModel**: Evaluaciones de proyectos por jurados
- **CriterioEvaluacionModel**: Criterios para evaluar proyectos
- **InscripcionModel**: Inscripciones de usuarios a hackatones
- **ParticipanteEquipoModel**: Relación entre participantes y equipos
- **CategoriaModel**: Categorías de hackatones
- **JuradoHackatonModel**: Asignación de jurados a hackatones

## 🔐 Seguridad

El sistema implementa:

- **Spring Security** para autenticación y autorización
- **BCrypt** para encriptación de contraseñas
- **OAuth 2.0** para login con Google
- **CSRF Protection**
- **Roles y Permisos** basados en rutas

## 🚦 Endpoints Principales

### Autenticación
- `GET /login` - Página de login
- `POST /auth/google` - Autenticación con Google
- `GET /logout` - Cerrar sesión

### Administrador
- `GET /admin/dashboard` - Dashboard administrativo
- `GET /admin/usuarios` - Lista de usuarios
- `POST /admin/usuarios` - Crear/editar usuario

### Participante
- `GET /participante/dashboard` - Dashboard de participante
- `GET /participante/equipos` - Gestión de equipos
- `POST /participante/proyectos` - Envío de proyectos

### Jurado
- `GET /jurado/dashboard` - Dashboard de jurado
- `GET /jurado/evaluar` - Evaluación de proyectos
- `POST /jurado/evaluaciones` - Guardar evaluaciones

## 🧪 Tests

Ejecutar los tests:

```bash
./mvnw test
```

## 📝 Notas de Desarrollo

- El sistema usa **JPA Hibernate** con estrategia `ddl-auto=update` para sincronizar automáticamente el esquema de base de datos
- Las plantillas Thymeleaf están organizadas por rol de usuario
- Se incluye validación tanto en el backend (Jakarta Validation) como en el frontend (JavaScript)
- El sistema genera códigos QR para usuarios (funcionalidad en `urlCodigoQr`)

## 🤝 Contribuir

Si deseas contribuir a este proyecto:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está desarrollado como parte de un hackathon educativo.

## 👨‍💻 Autor

JhonniTp

## 📞 Soporte

Para preguntas o soporte, por favor abre un issue en el repositorio de GitHub.

---

**Nota**: Este es un proyecto en desarrollo activo. Algunas características pueden estar en proceso de implementación.
