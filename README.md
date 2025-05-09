# Bug Bounty Platform

A marketplace where developers can find and solve real-world bugs in various applications.

## How to run

You will need docker. Docker desktop doesn't work on our work laptops, but you can get [Docker Engine through WSL](https://docs.docker.com/engine/install/ubuntu/).
Once you have that, from WSL, run `docker compose up --build`. This will run both the frontend and backend at the same time.
This [Docker compose video](https://www.youtube.com/watch?v=BTXfR76WmCw) explains it well if you want to look into it.


## Prerequisites

### Backend (Spring Boot)
- Java 17+
- MySQL 8.0+
- Gradle

### Frontend (Angular)
- Node.js 16+
- Angular CLI 16+

## Project Setup

### Database Configuration
1. Make sure MySQL is running
2. Database will be created automatically on first run
3. Check connection settings in `./project/bug-bounty-backend/src/main/resources/application.properties`

### Backend (Spring Boot)

1. Open a terminal and navigate to the backend folder:
   ```
   cd ./project/bug-bounty-backend
   ```

2. Run the project with Gradle:
   ```
   gradlew bootRun
   ```
   
3. The server will start on `http://localhost:8080`

### Frontend (Angular)

1. Open a new terminal and navigate to the frontend folder:
   ```
   cd ./project/front-end
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Start the development server:
   ```
   ng serve
   ```

4. Access the application at `http://localhost:4200`

## VSCode Configuration

### Recommended Extensions
- **Backend**: Extension Pack for Java, Spring Boot Extension Pack
- **Frontend**: Angular Language Service, ESLint

## Troubleshooting

### Backend Issues
- Make sure MySQL is running
- Check connection settings in `application.properties`
- Verify all Java files are in their correct packages

### Frontend Issues
- Make sure Node.js and Angular CLI are installed
- Check for CORS issues if API calls fail
- Try `npm install` if module errors occur

### If Everything Fails
- Reset the project to a clean state
- Follow the setup steps carefully again
- Make sure all prerequisites are installed correctly
