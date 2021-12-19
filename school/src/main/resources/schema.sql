DROP TABLE IF EXISTS User CASCADE;

CREATE TABLE User (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL
);

DROP TABLE IF EXISTS Course CASCADE;

CREATE TABLE Course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(500)
);

DROP TABLE IF EXISTS Enrollment;

CREATE TABLE Enrollment (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        enrollment_date TIMESTAMP,
                        course_id BIGINT,
                        user_id BIGINT,
                        FOREIGN KEY (course_id) REFERENCES Course(id),
                        FOREIGN KEY (user_id) REFERENCES User(id)
);

