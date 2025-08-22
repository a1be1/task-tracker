CREATE TABLE tasks(
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    priority TEXT NOT NULL,
    status TEXT NOT NULL,
    reporter_id INT REFERENCES users(id),
    confidential BOOLEAN NOT NULL,
    deadline DATE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);