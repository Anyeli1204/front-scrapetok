package com.example.scrapetok.config;

import com.example.scrapetok.domain.AdminProfile;
import com.example.scrapetok.domain.GeneralAccount;
import com.example.scrapetok.domain.QuestAndAnswer;
import com.example.scrapetok.domain.enums.Role;
import com.example.scrapetok.domain.enums.statusQA;
import com.example.scrapetok.repository.AdminProfileRepository;
import com.example.scrapetok.repository.GeneralAccountRepository;
import com.example.scrapetok.repository.QuestionAndAnswerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Testcontainers
@Import(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QuestAndAnswerRepositoryTest {
    @Autowired
    private QuestionAndAnswerRepository qaRepository;

    @Autowired
    private GeneralAccountRepository userRepo;

    @Autowired
    private AdminProfileRepository adminRepo;

    private GeneralAccount createUser(String email) {
        GeneralAccount user = new GeneralAccount();
        user.setEmail(email);
        user.setPassword("123");
        user.setFirstname("Nombre");
        user.setLastname("Apellido");
        user.setUsername(email.split("@")[0]);
        user.setRole(Role.USER);
        user.setCreationDate(LocalDate.now());
        return userRepo.save(user);
    }

    private AdminProfile createAdmin(String email) {
        GeneralAccount adminAcc = createUser(email);
        adminAcc.setRole(Role.ADMIN);
        userRepo.save(adminAcc);

        AdminProfile admin = new AdminProfile();
        admin.setUser(adminAcc);
        admin.setAdmisionToAdminDate(LocalDate.now());
        admin.setAdmisionToAdminTime(LocalTime.now());
        admin.setTotalQuestionsAnswered(0);
        admin.setIsActive(true);
        return adminRepo.save(admin);
    }

    @Test
    void testGuardarPreguntaPendiente() {
        GeneralAccount user = createUser("user1@mail.com");

        QuestAndAnswer qa = new QuestAndAnswer();
        qa.setUser(user);
        qa.setQuestionDescription("¿Cómo usar la app?");
        qa.setQuestionDate(LocalDate.now());
        qa.setQuestionHour(LocalTime.now());
        qa.setStatus(statusQA.PENDING);

        qaRepository.save(qa);

        QuestAndAnswer result = qaRepository.findById(qa.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(statusQA.PENDING);
        assertThat(result.getUser().getEmail()).isEqualTo("user1@mail.com");
    }

    @Test
    void testAdminRespondePregunta() {
        GeneralAccount user = createUser("user2@mail.com");
        AdminProfile admin = createAdmin("admin1@mail.com");

        QuestAndAnswer qa = new QuestAndAnswer();
        qa.setUser(user);
        qa.setQuestionDescription("¿Puedo ver métricas?");
        qa.setQuestionDate(LocalDate.now());
        qa.setQuestionHour(LocalTime.now());
        qa.setStatus(statusQA.PENDING);
        qaRepository.save(qa);

        // Admin responde
        qa.setAdmin(admin);
        qa.setAnswerDescription("Sí, puedes verlas desde tu panel.");
        qa.setAnswerDate(LocalDate.now());
        qa.setAnswerHour(LocalTime.now());
        qa.setStatus(statusQA.ANSWERED);
        qaRepository.save(qa);

        QuestAndAnswer result = qaRepository.findById(qa.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(statusQA.ANSWERED);
        assertThat(result.getAdmin().getUser().getEmail()).isEqualTo("admin1@mail.com");
    }


    @Test
    void testUsuarioConVariasPreguntas() {
        GeneralAccount user = createUser("user3@mail.com");

        for (int i = 1; i <= 3; i++) {
            QuestAndAnswer qa = new QuestAndAnswer();
            qa.setUser(user);
            qa.setQuestionDescription("Pregunta #" + i);
            qa.setQuestionDate(LocalDate.now());
            qa.setQuestionHour(LocalTime.now().plusMinutes(i));
            qa.setStatus(statusQA.PENDING);
            qaRepository.save(qa);
        }

        var preguntas = qaRepository.findAll().stream()
                .filter(q -> q.getUser().getEmail().equals("user3@mail.com"))
                .toList();

        assertThat(preguntas).hasSize(3);
    }

    @Test
    void testFiltrarSoloPreguntasPendientes() {
        GeneralAccount user = createUser("user4@mail.com");
        AdminProfile admin = createAdmin("admin2@mail.com");

        // Pregunta pendiente
        QuestAndAnswer p1 = new QuestAndAnswer();
        p1.setUser(user);
        p1.setQuestionDescription("Pendiente");
        p1.setQuestionDate(LocalDate.now());
        p1.setQuestionHour(LocalTime.now());
        p1.setStatus(statusQA.PENDING);
        qaRepository.save(p1);

        // Pregunta respondida
        QuestAndAnswer p2 = new QuestAndAnswer();
        p2.setUser(user);
        p2.setQuestionDescription("Respondida");
        p2.setQuestionDate(LocalDate.now());
        p2.setQuestionHour(LocalTime.now().plusMinutes(1));
        p2.setStatus(statusQA.ANSWERED);
        p2.setAdmin(admin);
        p2.setAnswerDescription("Respuesta");
        p2.setAnswerDate(LocalDate.now());
        p2.setAnswerHour(LocalTime.now().plusMinutes(2));
        qaRepository.save(p2);

        var pendientes = qaRepository.findAll().stream()
                .filter(q -> q.getStatus() == statusQA.PENDING)
                .toList();

        assertThat(pendientes).hasSize(1);
        assertThat(pendientes.get(0).getQuestionDescription()).isEqualTo("Pendiente");
    }

    @Test
    void testEliminarPregunta() {
        GeneralAccount user = createUser("user5@mail.com");

        QuestAndAnswer qa = new QuestAndAnswer();
        qa.setUser(user);
        qa.setQuestionDescription("Pregunta a eliminar");
        qa.setQuestionDate(LocalDate.now());
        qa.setQuestionHour(LocalTime.now());
        qa.setStatus(statusQA.PENDING);
        qaRepository.save(qa);

        Long id = qa.getId();
        qaRepository.deleteById(id);

        assertThat(qaRepository.findById(id)).isEmpty();
    }

    @Test
    void testPreguntaSinAdminNoTieneRespuesta() {
        GeneralAccount user = createUser("user7@mail.com");

        QuestAndAnswer qa = new QuestAndAnswer();
        qa.setUser(user);
        qa.setQuestionDescription("¿Sin admin?");
        qa.setQuestionDate(LocalDate.now());
        qa.setQuestionHour(LocalTime.now());
        qa.setStatus(statusQA.PENDING);
        qaRepository.save(qa);

        QuestAndAnswer result = qaRepository.findById(qa.getId()).orElseThrow();
        assertThat(result.getAdmin()).isNull();
        assertThat(result.getAnswerDescription()).isNull();
    }

    @Test
    void testBuscarPreguntasPorAdmin() {
        GeneralAccount user = createUser("user8@mail.com");
        AdminProfile admin = createAdmin("admin4@mail.com");

        QuestAndAnswer qa1 = new QuestAndAnswer();
        qa1.setUser(user);
        qa1.setQuestionDescription("Pregunta 1");
        qa1.setQuestionDate(LocalDate.now());
        qa1.setQuestionHour(LocalTime.now());
        qa1.setStatus(statusQA.ANSWERED);
        qa1.setAdmin(admin);
        qa1.setAnswerDescription("Respuesta 1");
        qa1.setAnswerDate(LocalDate.now());
        qa1.setAnswerHour(LocalTime.now());
        qaRepository.save(qa1);

        QuestAndAnswer qa2 = new QuestAndAnswer();
        qa2.setUser(user);
        qa2.setQuestionDescription("Pregunta 2");
        qa2.setQuestionDate(LocalDate.now());
        qa2.setQuestionHour(LocalTime.now().plusMinutes(1));
        qa2.setStatus(statusQA.PENDING);
        qaRepository.save(qa2);

        var respondidasPorAdmin = qaRepository.findAll().stream()
                .filter(q -> q.getAdmin() != null && q.getAdmin().getId().equals(admin.getId()))
                .toList();

        assertThat(respondidasPorAdmin).hasSize(1);
        assertThat(respondidasPorAdmin.get(0).getQuestionDescription()).isEqualTo("Pregunta 1");
    }
}