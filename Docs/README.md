# 🏡 Real Estate Property Listing Platform – Backend API

Spring Boot backend for a **Real Estate Property Listing Application** using **MySQL** with proper **Entity–Relationship (ER) mapping** and RESTful APIs.  
This backend is designed to work seamlessly with a React.js frontend.

For frontend 👇

https://github.com/atharvjamkhindikar/REAL_ESTATE_PROPERTY_LISTING_PLATFORM_FRONTEND

---

## 🚀 Project Overview

This backend powers a real-world real estate platform where users can:
- Browse and filter property listings
- Manage properties based on user roles
- Store multiple images per property
- Access secure, scalable REST APIs

The application follows **industry-standard layered architecture** and is optimized for **scalability and maintainability**.

---

## ✨ Key Features

- 🔐 User management (Agent, Owner, Buyer, Admin)
- 🏠 CRUD operations for property listings
- 🖼 Multiple images per property
- 🔍 Advanced property filtering:
  - City
  - Property type
  - Listing type (Sale/Rent)
  - Price range
- 🌐 RESTful API design
- 🔄 CORS enabled for React frontend
- 🗄 MySQL database with JPA/Hibernate
- 🔗 Proper Entity–Relationship mapping

---

## 🗄 Database Structure (MySQL)

### Entity Relationships
- **User ↔ Property**  
  One-to-Many  
  *(One user can list or own multiple properties)*

- **Property ↔ PropertyImage**  
  One-to-Many  
  *(One property can have multiple images)*

### Tables
1. **USERS** – Stores agents, owners, buyers, and admins  
2. **PROPERTIES** – Stores property listings  
3. **PROPERTY_IMAGES** – Stores images related to properties  

📊 See **DATABASE_SCHEMA.md** for detailed ER diagrams and table structures.

---

## 🛠 Tech Stack

| Layer        | Technology |
|--------------|-----------|
| Backend      | Spring Boot 3.2.1 |
| ORM          | Spring Data JPA / Hibernate |
| Database     | MySQL |
| Validation   | Bean Validation |
| Build Tool   | Maven |
| Utilities    | Lombok |

---

## ✅ Prerequisites

- Java **17 or higher**
- Maven **3.6+**
- MySQL **8.x**
- IDE (IntelliJ IDEA / Eclipse / VS Code)

---

## ⚙️ MySQL Configuration

Update your `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/realestate_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
