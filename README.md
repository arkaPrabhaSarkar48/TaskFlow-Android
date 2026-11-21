# TaskFlow – Android Task Management App

TaskFlow is an Android task management application developed using Java and XML. It allows users to organize daily tasks, manage their progress, and maintain task data locally using Room Database.

## Features

- **Create Tasks:** Add tasks with a title, category, and description.
- **Edit Tasks:** Update existing task details.
- **Delete Tasks:** Remove tasks with confirmation.
- **Task Completion:** Mark tasks as completed using checkboxes.
- **Persistent Storage:** Store task information locally using Room Database and SQLite.
- **Progress Tracking:** View dynamically calculated task completion progress.
- **RecyclerView:** Display tasks using a custom RecyclerView adapter.
- **Background Operations:** Perform database operations using ExecutorService.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java | Application development |
| Android SDK | Android platform |
| XML | UI layouts |
| Room Database | Local data persistence |
| SQLite | Underlying database |
| RecyclerView | Task list management |
| Material Components | User interface |
| ExecutorService | Background database operations |

## Project Structure

- `MainActivity` – Main dashboard and task management.
- `AddTaskActivity` – Task creation and editing.
- `Task` – Room database entity.
- `TaskDao` – Database operations.
- `TaskDatabase` – Room database configuration.
- `TaskAdapter` – RecyclerView adapter.

## Screenshots

Screenshots will be added soon.

## Getting Started

1. Clone this repository:

   ```bash
   git clone https://github.com/arkaPrabhaSarkar48/TaskFlow-Android.git
   ```

2. Open the project in Android Studio.
3. Allow Gradle to sync and download the required dependencies.
4. Run the application on an Android emulator or compatible device.

## Developer

Developed by **Arkaprabha Sarkar** as an Android development learning project.

**GitHub Repository:**  
https://github.com/arkaPrabhaSarkar48/TaskFlow-Android