package br.com.alura.school.course;

import br.com.alura.school.enrollment.Enrollment;
import br.com.alura.school.enrollment.EnrollmentRepository;
import br.com.alura.school.enrollment.NewEnrollmentRequest;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
class CourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    CourseController(CourseRepository courseRepository, UserRepository userRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping("/courses")
    ResponseEntity<List<CourseResponse>> allCourses() {
        List<CourseResponse> courseRespList = courseRepository.findAll().stream().map(x -> new CourseResponse(x)).collect(Collectors.toList());
        return ResponseEntity.ok().body(courseRespList);
    }

    @GetMapping("/courses/{code}")
    ResponseEntity<CourseResponse> courseByCode(@PathVariable("code") String code) {
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, format("Course with code %s not found", code)));
        return ResponseEntity.ok(new CourseResponse(course));
    }

    @PostMapping("/courses")
    ResponseEntity<Void> newCourse(@RequestBody @Valid NewCourseRequest newCourseRequest) {
        courseRepository.save(newCourseRequest.toEntity());
        URI location = URI.create(format("/courses/%s", newCourseRequest.getCode()));
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/courses/{courseCode}/enroll")
    ResponseEntity<Void> newEnroll(@PathVariable("courseCode") String courseCode, @RequestBody @Valid NewEnrollmentRequest request) {
        Course course = courseRepository.findByCode(courseCode).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, format("Course %s not found", courseCode)));
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, format("User with username %s not found", request.getUsername())));

        Optional<Enrollment> enrollment = enrollmentRepository.findByCourseCodeAndUserUsername(courseCode, request.getUsername());
        if (enrollment.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        enrollmentRepository.save(new Enrollment(course, user));
        return ResponseEntity.status(CREATED).build();
    }
}
