package br.upe.academia2.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.ui.controllers.ExercicioMenuController;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionModel;
import javafx.scene.layout.BorderPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExercicioMenuControllerTest {

    @InjectMocks
    ExercicioMenuController controller;

    @Mock
    private MultipleSelectionModel<Exercicio> selectionModelMock;

    @Mock
    private ListView<Exercicio> listaExercicios;
    @Mock
    private BorderPane mainPane;

    private ExercicioBusiness exercicioBusinessMock;

    private ReadOnlyObjectWrapper<Exercicio> selectedItemWrapper;
    @BeforeEach
    void setup() throws Exception {
        controller = new ExercicioMenuController();

        exercicioBusinessMock = mock(ExercicioBusiness.class);

        selectedItemWrapper = new ReadOnlyObjectWrapper<>();

        setMockField("exercicio", exercicioBusinessMock);
        setMockField("logger", mock(Logger.class));
        setMockField("listaExercicios", listaExercicios);
        lenient().doReturn(selectionModelMock).when(listaExercicios).getSelectionModel();
        lenient().doReturn(selectedItemWrapper.getReadOnlyProperty())
                .when(selectionModelMock).selectedItemProperty();
        controller.setMainPane(mainPane);
    }

    private void setMockField(String fieldName, Object mock) throws Exception {
        Field field = ExercicioMenuController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, mock);
    }
    @Test
    void AtualizarListView() {
        List<Exercicio> exerciciosSimulados = Arrays.asList(
                new Exercicio("Caminhada", "Aquecimento", null),
                new Exercicio("Flexão", "Peito e Tríceps", "caminho.gif")
        );

        when(exercicioBusinessMock.listarExercicios()).thenReturn(exerciciosSimulados);

        controller.refreshList();

        verify(exercicioBusinessMock).listarExercicios();
        // Captura o argumento passado para setItems() para verificar o conteúdo
        ArgumentCaptor<ObservableList<Exercicio>> captor = ArgumentCaptor.forClass(ObservableList.class);
        verify(listaExercicios).setItems(captor.capture());

        // o conteúdo da lista setada deve ser o esperado
        ObservableList<Exercicio> listaAtualizada = captor.getValue();
        assertNotNull(listaAtualizada);
        assertEquals(2, listaAtualizada.size());
        assertEquals("Caminhada", listaAtualizada.get(0).getNome());
    }

    @Test
    void initializeCarregarListaInicial() {
        // Stubbing para refreshList (evitar chamadas de business desnecessárias)
        when(exercicioBusinessMock.listarExercicios()).thenReturn(List.of());

        controller.initialize();

        verify(listaExercicios).setCellFactory(any());

        verify(listaExercicios).getSelectionModel();
        verify(selectionModelMock).selectedItemProperty();

        verify(exercicioBusinessMock).listarExercicios();
        verify(listaExercicios).setItems(any());
    }

    @Test
    void handleCadastrarExercicio() {
        verify(exercicioBusinessMock, never()).listarExercicios();
    }

}
