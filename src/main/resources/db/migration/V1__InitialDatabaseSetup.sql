CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  identity VARCHAR(255) NOT NULL,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  email VARCHAR(255),
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
  updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
  constraint uniq_identity unique (identity),
  constraint uniq_email unique (email)
);

-- CREATE FUNCTION update_updated_at_column() RETURNS trigger
--     LANGUAGE plpgsql
--     AS $$
--   BEGIN
--     NEW.updated_at = NOW();
--     RETURN NEW;
--   END;
-- $$;

-- CREATE TRIGGER users_updated_at_modtime BEFORE UPDATE ON users FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
