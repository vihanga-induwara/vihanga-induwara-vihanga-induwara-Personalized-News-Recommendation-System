# Personalized News Recommendation System

This project is developed as part of the CM2601 - Object-Oriented Development module coursework for the academic year 2024-2025. The goal is to implement a **Personalized News Recommendation System** using Object-Oriented Programming principles in Java, integrating MySQL for database operations, and utilizing a news API for fetching news articles.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Features](#features)
3. [Setup and Installation](#setup-and-installation)
   - [Prerequisites](#prerequisites)
   - [Step-by-Step Guide](#step-by-step-guide)
4. [Database Initialization](#database-initialization)
5. [API Integration](#api-integration)
6. [Running the Application](#running-the-application)
7. [License](#license)

---

## Project Overview

This project involves building a system that collects and recommends personalized news articles using Natural Language Processing (NLP) and Machine Learning (ML). The system tracks user interactions, stores preferences, and fetches articles from different sources via API. The recommendation engine analyzes user history and suggests relevant articles, adjusting over time as user preferences evolve.

---

## Features

1. **User Management**: Users can register, log in, and manage their preferences. Their reading history is tracked.
2. **Article Processing**: Articles fetched from various sources and categorized using NLP techniques.
3. **Recommendation Engine**: Recommends news articles based on user reading history using ML algorithms.
4. **AI/ML Integration**: Includes sentiment analysis or categorization based on pre-trained models.
5. **Exception Handling**: Handles invalid inputs, file I/O issues, API failures, etc.
6. **Concurrency**: Handles multiple users and processes concurrently for smoother performance.

---

## Setup and Installation

### Prerequisites

Before setting up the project, ensure you have the following installed:

- **Java Development Kit (JDK) 8 or higher**
- **MySQL Server**
- **An IDE (e.g., IntelliJ IDEA, Eclipse)**
- **Maven or Gradle (Optional for dependency management)**
- **Internet Connection (for News API integration)**

### Step-by-Step Guide

1. **Clone the Repository**
   - Start by cloning the repository from GitHub:
     ```bash
     git clone https://github.com/yourusername/NewsRecommendationSystem.git
     cd NewsRecommendationSystem
     ```

2. **Install MySQL Database**
   - Download and install **MySQL** from [here](https://dev.mysql.com/downloads/).
   - Create a new MySQL database for the application. 

3. **Set Up MySQL Database**

   1. Open the `MySQL Workbench` or use the command line to connect to the MySQL server.
   2. Run the following MySQL script to initialize the database:
      ```sql
      -- phpMyAdmin SQL Dump
      -- version 5.2.1
      -- https://www.phpmyadmin.net/
      --
      -- Host: 127.0.0.1
      -- Generation Time: Dec 05, 2024 at 02:27 PM
      -- Server version: 10.4.32-MariaDB
      -- PHP Version: 8.2.12

      SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
      START TRANSACTION;
      SET time_zone = "+00:00";

      CREATE DATABASE newsapp;

      USE newsapp;

      -- Create Users Table
      CREATE TABLE users (
          user_id INT AUTO_INCREMENT PRIMARY KEY,
          username VARCHAR(50) NOT NULL,
          password VARCHAR(255) NOT NULL,
          email VARCHAR(100),
          preferences TEXT,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );

      -- Create Articles Table
      CREATE TABLE articles (
          article_id INT AUTO_INCREMENT PRIMARY KEY,
          title VARCHAR(255) NOT NULL,
          content TEXT,
          category VARCHAR(50),
          published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );

      -- Create UserHistory Table
      CREATE TABLE user_history (
          history_id INT AUTO_INCREMENT PRIMARY KEY,
          user_id INT NOT NULL,
          article_id INT NOT NULL,
          action ENUM('read', 'liked', 'skipped') NOT NULL,
          action_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          FOREIGN KEY (user_id) REFERENCES users(user_id),
          FOREIGN KEY (article_id) REFERENCES articles(article_id)
      );

      COMMIT;
      ```
   - This script will create the necessary tables (`users`, `articles`, `user_history`) to store user information, articles, and their interactions.

4. **Configure Database Connection**
   - In the project, locate the `DatabaseConfig.java` file (or equivalent) and configure it with your MySQL database credentials:
     ```java
     public class DatabaseConfig {
         public static final String DB_URL = "jdbc:mysql://localhost:3306/newsapp";
         public static final String DB_USER = "root";
         public static final String DB_PASSWORD = "yourpassword";
     }
     ```

---

## API Integration

1. **Get Your News API Key**
   - Sign up on a news API platform such as [NewsAPI](https://newsapi.org/) and obtain an API key.

2. **Configure News API Integration**
   - In the project, locate the `NewsApiFetcher.java` file and update the API key:
     ```java
     public class NewsApiFetcher {
         private static final String API_KEY = "your-api-key-here";
         private static final String BASE_URL = "https://newsapi.org/v2/top-headlines";
         // Additional API integration code
     }
     ```

---

## Running the Application

1. **Build the Project**
   - If using Maven, run:
     ```bash
     mvn clean install
     ```
   - If using Gradle, run:
     ```bash
     gradle build
     ```

2. **Run the Application**
   - Launch the application from your IDE or use the command line to run the main class:
     ```bash
     java -jar NewsRecommendationSystem.jar
     ```

3. **Access the Application**
   - The application will be running locally on your system. You can interact with the console-based UI or implement a basic GUI for user interactions.

---

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

---

## University Details

- **University**: RGU University
- **Module**: CM2601 - Object-Oriented Development
- **Academic Year**: 2024-2025
- **Assessment Type**: Individual Coursework
- **Module Coordinator**: Malsha Fernando
