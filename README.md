# CS584 Project - [Your Name]

**Course:** CS584 Software Engineering in Practice  
**Semester:** Spring 2026  
**Instructor:** Chase Fensore

## Project Description

DiaCare Egypt is a web application designed to help users monitor and prevent secondary complications of diabetes. 

The system provides tools for tracking personal health information, monitoring screening schedules (such as A1C, eye, and kidney tests), and performing simple daily self-checks like foot symptom checks. It aims to encourage proactive health management through reminders and basic risk alerts.

## Problem Statement

Diabetes patients are at risk of developing serious secondary complications such as eye disease, kidney damage, and cardiovascular issues. However, many patients lack consistent monitoring, timely screenings, and daily self-awareness of symptoms.

This project targets diabetes patients who need a simple and accessible way to manage their health. The goal is to help users stay on track with recommended screenings, monitor daily conditions, and become more aware of potential risks.

## Proposed Solution

The system is a web-based application that allows users to manage their diabetes-related health information and stay on track with recommended care.

Key features include:

- **User authentication** for secure access  
- **Health profile initialization**, where users provide basic medical information  
- **Dashboard overview** showing health status, screening progress, and risk alerts  
- **Screening tracking system** that calculates next due dates and highlights overdue or upcoming tests  
- **Medication tracking** to record daily intake  
- **Appointment scheduling** for upcoming screenings  
- **Foot symptom check**, a simple daily questionnaire to detect potential issues early  
- **Basic education modules** to help users understand risks and recommended checkups  

The system is designed as a minimum viable product (MVP), focusing on core features that support daily self-management and early risk awareness.

## Tech Stack

## Tech Stack

- **Frontend:** Vue 3, Vite  
- **Backend:** Spring Boot, Spring Security, JWT, MyBatis  
- **Database:** MySQL  
- **Deployment:** Google Cloud Run  

## Setup Instructions

Follow these steps to run the project locally:

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd cs584project
```
### 2. Setup the database
-Create a MySQL database named diabetes_mvp
-Import the provided SQL file
-Update your database credentials in the .env file
### 3. Run the backend
```bash
cd backend
mvn spring-boot:run
```
### 4. Run the frontend
```bash
cd frontend
npm install
npm run dev
```
### 5. Open the application
Visit:http://localhost:5173
## Project Status

- [ ] Requirements gathering
- [ ] SOW completed
- [ ] Prototype/mockup
- [ ] MVP development
- [ ] User testing
- [ ] Final delivery

## Links

- **Live Demo:** [URL once deployed]
- **Documentation:** [Link to docs folder]
- **Project Board:** [Link to GitHub Project]

## Collaborators

- **Developer:** [Your name] (@yourusername)
- **Instructor:** Chase Fensore (@fensorechase)

---

*Last updated: [Date]*
