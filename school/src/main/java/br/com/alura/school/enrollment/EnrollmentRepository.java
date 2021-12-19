package br.com.alura.school.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByCourseCodeAndUserUsername(String courseCode, String username);
}