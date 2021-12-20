package br.com.alura.school.enrollment;

import br.com.alura.school.course.Course;
import br.com.alura.school.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "UQ_Enrollment_CourseId_UserId", columnNames = { "course_id", "user_id" }) })
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime enrollment_date = LocalDateTime.now();

    @JoinColumn(name = "course_id")
    @ManyToOne
    private Course course;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Deprecated
    protected Enrollment() {}

    public Enrollment(Course course, User user) {
        this.course = course;
        this.user = user;
    }

    public Enrollment(Long id, LocalDateTime enrollment_date, Course course, User user) {
        this.id = id;
        this.enrollment_date = enrollment_date;
        this.course = course;
        this.user = user;
    }

    public LocalDateTime getEnrollment_date() {
        return enrollment_date;
    }

    public Course getCourse() {
        return course;
    }

    public User getUser() {
        return user;
    }
}
