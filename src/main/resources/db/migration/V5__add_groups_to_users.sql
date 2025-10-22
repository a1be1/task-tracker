ALTER TABLE users
ADD COLUMN group_id INT,
ADD CONSTRAINT fk_group
FOREIGN KEY (group_id) REFERENCES groups(id);