
# Full Stack Game Rental App

Team Members: Nishant Tiwari, Ynah Novilla

## Overview
This is a full-stack web application for an online game rental service, developed using **Java**, **PostgreSQL**, and **SQL**. The system allows users to browse, rent games, manage inventory, and handle customer details, featuring role-based access and a secure login mechanism. The project simulates a real-world game rental platform.
Please refer to documentation for detailed explanations of each functions
## Features
1. **User Management**
   - Create, read, update, and delete customer profiles.
   - Role-based login system for managers, employees, and customers.

2. **Game Inventory Management**
   - Add, update, delete, and view game inventory.

3. **Secure Authentication**
   - Login functionality with password encryption and session management.
   - Role-based access control: Managers have full access, while employees and customers have limited access.

4. **Role-Based Access Control**
   - **Managers**: Can manage games, customers, and employees.
   - **Employees**: Can manage games and view customer data.
   - **Customers**: Can view games and their rental history.

## Database
- **Users**: Stores user information and role (customer, employee, manager).
- **Games**: Stores game inventory details.
- **Rentals**: Tracks rental transactions.
- **Roles**: Manages role-based permissions for users.


## Technologies Used
- **Java**
- **PostgreSQL**
- **SQL**
