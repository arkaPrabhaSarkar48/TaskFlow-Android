package com.app.taskflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvTasks;
    private ExtendedFloatingActionButton btnAddTask;
    private TextView tvGreeting, tvTaskCount;
    private LinearProgressIndicator progressTasks;

    private List<Task> taskList = new ArrayList<>();

    private TaskAdapter taskAdapter;

    private ActivityResultLauncher<Intent> addTaskLauncher;

    private TextView tvProgressPercentage;

    private TaskDatabase taskDatabase;

    private ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null) {

                        Intent data = result.getData();

                        String title = data.getStringExtra("task_title");
                        String category = data.getStringExtra("task_category");
                        String description = data.getStringExtra("task_description");

                        boolean isEditMode =
                                data.getBooleanExtra("is_edit_mode", false);

                        if (isEditMode) {

                            // Get the existing task ID
                            int taskId = data.getIntExtra("task_id", -1);

                            for (int i = 0; i < taskList.size(); i++) {

                                Task task = taskList.get(i);

                                if (task.getId() == taskId) {

                                    task.setTitle(title);
                                    task.setCategory(category);
                                    task.setDescription(description);
                                    final int position = i;

                                    databaseExecutor.execute(() -> {

                                        taskDatabase.taskDao().updateTask(task);

                                        runOnUiThread(() -> {

                                            taskAdapter.notifyItemChanged(position);

                                            updateTaskProgress();

                                        });

                                    });

                                    break;
                                }
                            }

                        } else {

                            // Create a new task
                            Task newTask = new Task(
                                    title,
                                    description,
                                    false,
                                    category
                            );

                            databaseExecutor.execute(() -> {

                                long newTaskId = taskDatabase.taskDao()
                                        .insertTask(newTask);

                                newTask.setId((int) newTaskId);

                                runOnUiThread(() -> {

                                    taskList.add(newTask);

                                    taskAdapter.notifyItemInserted(
                                            taskList.size() - 1
                                    );

                                    rvTasks.scrollToPosition(taskList.size() - 1);

                                    updateTaskProgress();

                                });

                            });
                        }

                        // Refresh dashboard
                        updateTaskProgress();
                    }
                }
        );

        initViews();

        taskDatabase = TaskDatabase.getInstance(this);

        setupRecyclerView();

        setupTaskAdapter();

        loadTasksFromDatabase();

        setupClickListeners();

    }

    private void initViews() {

        rvTasks = findViewById(R.id.rvTasks);
        btnAddTask = findViewById(R.id.btnAddTask);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvTaskCount = findViewById(R.id.tvTaskCount);
        progressTasks = findViewById(R.id.progressTasks);
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage);

    }

    private void setupRecyclerView() {

        rvTasks.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Adapter will be connected here
    }



    private void showDeleteConfirmation(Task task) {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete \""
                        + task.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    int position = taskList.indexOf(task);

                    if (position != -1) {
                        databaseExecutor.execute(() -> {

                            taskDatabase.taskDao().deleteTask(task);

                            runOnUiThread(() -> {

                                int currentPosition = taskList.indexOf(task);

                                if (currentPosition != -1) {

                                    taskList.remove(currentPosition);

                                    taskAdapter.notifyItemRemoved(currentPosition);

                                    updateTaskProgress();
                                }

                            });

                        });
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupTaskAdapter() {

        taskAdapter = new TaskAdapter(
                taskList,
                new TaskAdapter.OnTaskStatusChangedListener() {

                    @Override
                    public void onTaskStatusChanged(Task task) {

                        updateTaskProgress();

                        databaseExecutor.execute(() -> {
                            taskDatabase.taskDao().updateTask(task);
                        });
                    }

                    @Override
                    public void onTaskClicked(Task task) {
                        openEditTask(task);
                    }

                    @Override
                    public void onTaskLongClicked(Task task) {
                        showDeleteConfirmation(task);
                    }
                }
        );

        rvTasks.setAdapter(taskAdapter);
    }

    private void loadTasksFromDatabase() {

        databaseExecutor.execute(() -> {

            List<Task> savedTasks =
                    taskDatabase.taskDao().getAllTasks();

            runOnUiThread(() -> {

                taskList.clear();
                taskList.addAll(savedTasks);

                taskAdapter.notifyDataSetChanged();

                updateTaskProgress();
            });

        });
    }

    private void setupClickListeners() {

        btnAddTask.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            addTaskLauncher.launch(intent);

        });

    }

    private void openEditTask(Task task) {

        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);

        intent.putExtra("is_edit_mode", true);
        intent.putExtra("task_id", task.getId());
        intent.putExtra("task_title", task.getTitle());
        intent.putExtra("task_category", task.getCategory());
        intent.putExtra("task_description", task.getDescription());

        addTaskLauncher.launch(intent);
    }

    private void updateTaskProgress() {

        int totalTasks = taskList.size();
        int completedTasks = 0;

        for (Task task : taskList) {
            if (task.isCompleted()) {
                completedTasks++;
            }
        }

        int percentage = 0;

        if (totalTasks > 0) {
            percentage = (completedTasks * 100) / totalTasks;
        }

        // Update progress bar
        progressTasks.setProgress(percentage);

        // Update percentage text
        tvProgressPercentage.setText(percentage + "%");

        // Update task count
        tvTaskCount.setText(
                "You have " + totalTasks + " tasks today"
        );
    }
}