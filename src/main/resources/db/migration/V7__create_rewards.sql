CREATE TABLE rewards(
    id TEXT PRIMARY KEY,
    task_id TEXT REFERENCES tasks(id),
    user_id INT REFERENCES users(id),
    updated_by INT REFERENCES users(id),
    amount INT NOT NULL,
    description TEXT,
    total_sum INT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);