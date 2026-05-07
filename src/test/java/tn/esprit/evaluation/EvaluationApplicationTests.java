package tn.esprit.evaluation;

// ===== IMPORTS =====
import com.fasterxml.jackson.databind.ObjectMapper;
import tn.esprit.evaluation.entite.Evaluation;
import tn.esprit.evaluation.entite.TypeEvaluation;
import tn.esprit.evaluation.service.IEvaluationService;
import tn.esprit.evaluation.repository.EvaluationRepository;
import tn.esprit.evaluation.service.EvaluationServiceImpl;
import tn.esprit.evaluation.controller.EvaluationController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ============================================================
// 1. APPLICATION CONTEXT TEST
// ============================================================
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EvaluationApplicationTests {

    @Test
    void contextLoads() {
    }
}

// ============================================================
// 2. CONTROLLER TEST
// ============================================================
@WebMvcTest(EvaluationController.class)
@AutoConfigureMockMvc(addFilters = false)
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEvaluationService evaluationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Evaluation evaluation;

    @BeforeEach
    void setUp() {
        evaluation = Evaluation.builder()
                .idEvaluation(1L)
                .missionId(1L)
                .evaluateurId(5L)
                .evalueId(10L)
                .typeEvaluation(TypeEvaluation.FREELANCE)
                .noteQualite(5)
                .noteDelais(4)
                .noteCommunication(5)
                .noteGlobal(4.7)
                .commentaire("Excellent travail")
                .dateEvaluation(LocalDate.now())
                .build();
    }

    @Test
    void shouldGetEvaluationById() throws Exception {
        when(evaluationService.retrieveEvaluation(1L)).thenReturn(evaluation);

        mockMvc.perform(get("/api/competence/evaluations/retrieve-evaluation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEvaluation").value(1L));
    }

    @Test
    void shouldAddEvaluation() throws Exception {
        when(evaluationService.addEvaluation(any(Evaluation.class))).thenReturn(evaluation);

        mockMvc.perform(post("/api/competence/evaluations/add-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evaluation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEvaluation").value(1L));
    }

    @Test
    void shouldUpdateEvaluation() throws Exception {
        evaluation.setCommentaire("Modifié avec succès");
        when(evaluationService.modifyEvaluation(any(Evaluation.class))).thenReturn(evaluation);

        mockMvc.perform(put("/api/competence/evaluations/modify-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evaluation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentaire").value("Modifié avec succès"));
    }

    @Test
    void shouldDeleteEvaluation() throws Exception {
        doNothing().when(evaluationService).removeEvaluation(1L);

        mockMvc.perform(delete("/api/competence/evaluations/remove-evaluation/1"))
                .andExpect(status().isOk());
    }
}

// ============================================================
// 3. REPOSITORY TEST
// ============================================================
@DataJpaTest
class EvaluationRepositoryTest {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Test
    void shouldSaveEvaluation() {
        Evaluation evaluation = Evaluation.builder()
                .missionId(1L)
                .evaluateurId(5L)
                .evalueId(10L)
                .typeEvaluation(TypeEvaluation.FREELANCE)
                .noteQualite(5)
                .noteDelais(4)
                .noteCommunication(5)
                .noteGlobal(4.7)
                .commentaire("Excellent travail")
                .dateEvaluation(LocalDate.now())
                .build();

        Evaluation saved = evaluationRepository.save(evaluation);

        assertThat(saved.getIdEvaluation()).isNotNull();
    }

    @Test
    void shouldFindEvaluationById() {
        Evaluation evaluation = Evaluation.builder()
                .missionId(2L)
                .evaluateurId(3L)
                .evalueId(7L)
                .typeEvaluation(TypeEvaluation.FREELANCE)
                .noteQualite(3)
                .noteDelais(3)
                .noteCommunication(4)
                .noteGlobal(3.3)
                .commentaire("Bon travail")
                .dateEvaluation(LocalDate.now())
                .build();

        Evaluation saved = evaluationRepository.save(evaluation);
        Evaluation found = evaluationRepository.findById(saved.getIdEvaluation()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getCommentaire()).isEqualTo("Bon travail");
    }

    @Test
    void shouldDeleteEvaluation() {
        Evaluation evaluation = Evaluation.builder()
                .missionId(3L)
                .evaluateurId(4L)
                .evalueId(8L)
                .typeEvaluation(TypeEvaluation.FREELANCE)
                .noteQualite(4)
                .noteDelais(4)
                .noteCommunication(4)
                .noteGlobal(4.0)
                .commentaire("À supprimer")
                .dateEvaluation(LocalDate.now())
                .build();

        Evaluation saved = evaluationRepository.save(evaluation);
        evaluationRepository.deleteById(saved.getIdEvaluation());

        assertThat(evaluationRepository.findById(saved.getIdEvaluation())).isEmpty();
    }
}

// ============================================================
// 4. SERVICE TEST (CORRIGÉ)
// ============================================================
@ExtendWith(MockitoExtension.class)
class EvaluationServiceImplTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private EvaluationServiceImpl evaluationService;

    private Evaluation evaluation;

    @BeforeEach
    void setUp() {
        evaluation = Evaluation.builder()
                .idEvaluation(1L)
                .missionId(1L)
                .evaluateurId(5L)
                .evalueId(10L)
                .typeEvaluation(TypeEvaluation.FREELANCE)
                .noteQualite(5)
                .noteDelais(4)
                .noteCommunication(5)
                .noteGlobal(4.7)
                .commentaire("Excellent travail")
                .dateEvaluation(LocalDate.now())
                .build();
    }

    @Test
    void shouldRetrieveEvaluationById() {
        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(evaluation));

        Evaluation found = evaluationService.retrieveEvaluation(1L);

        assertThat(found).isNotNull();
        assertThat(found.getIdEvaluation()).isEqualTo(1L);
        verify(evaluationRepository, times(1)).findById(1L);
    }

    @Test
    void shouldAddEvaluation() {
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(evaluation);

        Evaluation result = evaluationService.addEvaluation(evaluation);

        assertThat(result).isNotNull();
        assertThat(result.getNoteGlobal()).isEqualTo(4.7);
        verify(evaluationRepository, times(1)).save(evaluation);
    }

    @Test
    void shouldModifyEvaluation() {
        evaluation.setCommentaire("Modifié");
        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(evaluation));
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(evaluation);

        Evaluation result = evaluationService.modifyEvaluation(evaluation);

        assertThat(result.getCommentaire()).isEqualTo("Modifié");
        verify(evaluationRepository, times(1)).save(evaluation);
    }

    @Test
    void shouldDeleteEvaluation() {
        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(evaluation));
        doNothing().when(evaluationRepository).delete(evaluation);

        evaluationService.removeEvaluation(1L);

        verify(evaluationRepository, times(1)).delete(evaluation);
    }
}