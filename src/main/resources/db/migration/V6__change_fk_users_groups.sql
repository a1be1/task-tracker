ALTER TABLE users
DROP CONSTRAINT fk_group,
ADD CONSTRAINT fk_group
    FOREIGN KEY (group_id)
    REFERENCES groups (id)
    ON DELETE CASCADE;