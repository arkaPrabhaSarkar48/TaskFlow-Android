package com.app.taskflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddTaskActivity extends AppCompatActivity {

    private TextInputEditText etTaskTitle;
    private TextInputEditText etTaskCategory;
    private TextInputEditText etTaskDescription;

    private MaterialButton btnSaveTask;

    private boolean isEditMode = false;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();

        checkEditMode();

        setupClickListeners();
    }

    private void initViews() {

        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskCategory = findViewById(R.id.etTaskCategory);
        etTaskDescription = findViewById(R.id.etTaskDescription);

        btnSaveTask = findViewById(R.id.btnSaveTask);
    }

    private void checkEditMode() {

        Intent intent = getIntent();

        isEditMode = intent.getBooleanExtra("is_edit_mode", false);

        if (isEditMode) {

            taskId = intent.getIntExtra("task_id", -1);

            String title = intent.getStringExtra("task_title");
            String category = intent.getStringExtra("task_category");
            String description = intent.getStringExtra("task_description");

            etTaskTitle.setText(title);
            etTaskCategory.setText(category);
            etTaskDescription.setText(description);

            btnSaveTask.setText("Update Task");
        }
    }

    private void setupClickListeners() {

        btnSaveTask.setOnClickListener(v -> {

            String title = etTaskTitle.getText().toString().trim();
            String category = etTaskCategory.getText().toString().trim();
            String description = etTaskDescription.getText().toString().trim();

            if (title.isEmpty()) {
                etTaskTitle.setError("Task title is required");
                etTaskTitle.requestFocus();
                return;
            }

            if (category.isEmpty()) {
                category = "General";
            }

            Intent resultIntent = new Intent();

            resultIntent.putExtra("task_id", taskId);
            resultIntent.putExtra("task_title", title);
            resultIntent.putExtra("task_category", category);
            resultIntent.putExtra("task_description", description);
            resultIntent.putExtra("is_edit_mode", isEditMode);

            setResult(RESULT_OK, resultIntent);

            finish();
        });
    }
}