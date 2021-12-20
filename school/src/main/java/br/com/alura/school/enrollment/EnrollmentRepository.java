package br.com.alura.school.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByCourseCodeAndUserUsername(String courseCode, String username);

    @Query(value = "SELECT u.email AS email, COUNT(u.id) AS quantidade_matriculas"
            + " FROM USER u"
            + " INNER JOIN enrollment e"
            + " WHERE u.id = e.user_id"
            + " GROUP BY u.id, u.email"
            + " ORDER BY quantidade_matriculas DESC", nativeQuery = true)
    List<EnrollmentResponse> countEnrollsAndUsername();
}