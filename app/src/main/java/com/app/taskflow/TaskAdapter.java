package com.app.taskflow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    public interface OnTaskStatusChangedListener {
        void onTaskStatusChanged(Task task);
        void onTaskClicked(Task task);
        void onTaskLongClicked(Task task);
    }

    private OnTaskStatusChangedListener listener;

    public TaskAdapter(
            List<Task> taskList,
            OnTaskStatusChangedListener listener
    ) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TaskViewHolder holder, int position) {

        Task task = taskList.get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());
        holder.tvCategory.setText(task.getCategory());

        // Prevent old listeners from reacting during recycling
        holder.cbTaskCompleted.setOnCheckedChangeListener(null);

        holder.cbTaskCompleted.setChecked(task.isCompleted());

        holder.cbTaskCompleted.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    task.setCompleted(isChecked);

                    if (listener != null) {
                        listener.onTaskStatusChanged(task);
                    }
                }
        );
        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onTaskClicked(task);
            }

        });
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onTaskLongClicked(task);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription, tvCategory;
        CheckBox cbTaskCompleted;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvCategory = itemView.findViewById(R.id.tvTaskCategory);
            cbTaskCompleted = itemView.findViewById(R.id.cbTaskCompleted);
        }
    }
}