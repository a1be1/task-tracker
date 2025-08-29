CREATE TABLE executors_tasks(
    user_id INT NOT NULL REFERENCES users(id),
    task_id TEXT NOT NULL REFERENCES tasks(id)
);