package br.upe.academia2.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.ui.controllers.CadastrarPlanoTreinoController;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarPlanoTreinoControllerTest {

    @InjectMocks
    private CadastrarPlanoTreinoController controller;

    @Mock
    private PlanoTreinoBusiness planoTreinoBusiness;

    private TextField nomeField;
    private DatePicker dataInicioPicker;
    private DatePicker dataFimPicker;
    private String mensagem;
    private Usuario usuario;

    @BeforeEach
    void setUp() throws Exception {
        nomeField = new TextField();
        dataInicioPicker = new DatePicker();
        dataFimPicker = new DatePicker();
        usuario = mock(Usuario.class);

        controller = new CadastrarPlanoTreinoController() {
            @Override
            public void mostrarAlerta(String titulo, String msg, javafx.scene.control.Alert.AlertType tipo) {
                mensagem = msg;
            }
        };

        var fNome = CadastrarPlanoTreinoController.class.getDeclaredField("nomeField");
        fNome.setAccessible(true);
        fNome.set(controller, nomeField);

        var fInicio = CadastrarPlanoTreinoController.class.getDeclaredField("dataInicioPicker");
        fInicio.setAccessible(true);
        fInicio.set(controller, dataInicioPicker);

        var fFim = CadastrarPlanoTreinoController.class.getDeclaredField("dataFimPicker");
        fFim.setAccessible(true);
        fFim.set(controller, dataFimPicker);

        var fBusiness = CadastrarPlanoTreinoController.class.getDeclaredField("planoTreinoBusiness");
        fBusiness.setAccessible(true);
        fBusiness.set(controller, planoTreinoBusiness);

        controller.setUsuarioLogado(usuario);
    }

    @Test
    void testCamposVazios() {
        nomeField.setText("");
        dataInicioPicker.setValue(null);
        dataFimPicker.setValue(null);

        controller.handleCadastrar();

        assertEquals("Preencha todos os campos antes de salvar.", mensagem);
        verifyNoInteractions(planoTreinoBusiness);
    }

    @Test
    void testDataInicioMaiorQueFim() {
        nomeField.setText("Treino A");
        dataInicioPicker.setValue(LocalDate.of(2025, 2, 10));
        dataFimPicker.setValue(LocalDate.of(2025, 2, 5));

        controller.handleCadastrar();

        assertEquals("A data de início não pode ser depois da data de término.", mensagem);
        verifyNoInteractions(planoTreinoBusiness);
    }

    @Test
    void testCadastroSucesso() {
        nomeField.setText("Treino Superior");
        dataInicioPicker.setValue(LocalDate.of(2025, 3, 1));
        dataFimPicker.setValue(LocalDate.of(2025, 3, 15));

        doNothing().when(planoTreinoBusiness).cadastrarPlanoDeTreino(any(), any());

        controller.handleCadastrar();

        assertEquals("Plano de treino cadastrado com sucesso!", mensagem);
        verify(planoTreinoBusiness).cadastrarPlanoDeTreino(eq(usuario), any(PlanoTreino.class));
    }

    @Test
    void testErroAoCadastrar() {
        nomeField.setText("Treino Força");
        dataInicioPicker.setValue(LocalDate.of(2025, 5, 1));
        dataFimPicker.setValue(LocalDate.of(2025, 5, 20));

        doThrow(new RuntimeException("Falha no CSV"))
                .when(planoTreinoBusiness).cadastrarPlanoDeTreino(any(), any());

        controller.handleCadastrar();

        assertEquals("Erro ao cadastrar o plano: Falha no CSV", mensagem);
        verify(planoTreinoBusiness).cadastrarPlanoDeTreino(eq(usuario), any(PlanoTreino.class));
    }
}
