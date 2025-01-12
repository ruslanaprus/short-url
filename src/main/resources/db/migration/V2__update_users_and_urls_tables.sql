-- Rename column 'short_url' to 'short_code' in the 'urls' table
ALTER TABLE urls
    RENAME COLUMN short_url TO short_code;

-- Drop column 'username' from the 'users' table
ALTER TABLE users
    DROP COLUMN username;

-- Drop column 'role' from the 'users' table
ALTER TABLE users
    DROP COLUMN role;

-- Remove the password length and content check from 'users' table
-- Drop the CHECK constraint on 'password' if it exists
ALTER TABLE users
    DROP CONSTRAINT IF EXISTS users_password_check;

-- Optionally, ensure 'password' remains NOT NULL (if required)
ALTER TABLE users
    ALTER COLUMN password SET NOT NULL;

