BEGIN
CREATE TABLE privileges (
    id bigserial PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_privileges_name ON privileges(name);
CREATE INDEX idx_privileges_category ON privileges(category);
COMMIT;