package br.com.alura.school.course;

import br.com.alura.school.enrollment.Enrollment;
import br.com.alura.school.enrollment.EnrollmentRepository;
import br.com.alura.school.enrollment.NewEnrollmentRequest;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CourseControllerTest {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void should_retrieve_course_by_code() throws Exception {
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));

        mockMvc.perform(get("/courses/java-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("java-1")))
                .andExpect(jsonPath("$.name", is("Java OO")))
                .andExpect(jsonPath("$.shortDescription", is("Java and O...")));
    }

    @Test
    void should_retrieve_all_courses() throws Exception {
        courseRepository.save(new Course("spring-1", "Spring Basics", "Spring Core and Spring MVC."));
        courseRepository.save(new Course("spring-2", "Spring Boot", "Spring Boot"));

        mockMvc.perform(get("/courses")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].code", is("spring-1")))
                .andExpect(jsonPath("$[0].name", is("Spring Basics")))
                .andExpect(jsonPath("$[0].shortDescription", is("Spring Cor...")))
                .andExpect(jsonPath("$[1].code", is("spring-2")))
                .andExpect(jsonPath("$[1].name", is("Spring Boot")))
                .andExpect(jsonPath("$[1].shortDescription", is("Spring Boot")));
    }

    @Test
    void should_add_new_course() throws Exception {
        NewCourseRequest newCourseRequest = new NewCourseRequest("java-2", "Java Collections", "Java Collections: Lists, Sets, Maps and more.");

        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newCourseRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/courses/java-2"));
    }

    @Test
    void should_add_new_enrollment() throws Exception {
        Course course = courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));
        User user = userRepository.save(new User("alex", "alex@email.com"));

        NewEnrollmentRequest request = new NewEnrollmentRequest(user.getUsername());
        mockMvc.perform(post(String.format("/courses/%s/enroll", course.getCode()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void should_not_found_if_course_does_not_exist() throws Exception {
        Course course = new Course("java-0", "Java error", "An exception in Java is an object of class java. lang. Exception, or one of its subclasses");
        User user = userRepository.save(new User("alex", "alex@email.com"));

        NewEnrollmentRequest request = new NewEnrollmentRequest(user.getUsername());
        mockMvc.perform(post(String.format("/courses/%s/enroll", course.getCode()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_not_found_if_user_does_not_exist() throws Exception {
        Course course = courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));
        User user = new User("Jonathan", "Jonathan@email.com");

        NewEnrollmentRequest request = new NewEnrollmentRequest(user.getUsername());
        mockMvc.perform(post(String.format("/courses/%s/enroll", course.getCode()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_bad_request_if_user_is_already_enrolled_in_the_course() throws Exception {
        Course course = courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));
        User user = userRepository.save(new User("alex", "alex@email.com"));
        enrollmentRepository.save(new Enrollment(course, user));

        NewEnrollmentRequest request = new NewEnrollmentRequest(user.getUsername());
        mockMvc.perform(post(String.format("/courses/%s/enroll", course.getCode()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}