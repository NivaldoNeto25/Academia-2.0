package br.upe.academia2.controllers;

import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.repository.IndBioRepoImpl;
import br.upe.academia2.ui.controllers.IndicadoresAlunoController;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadoresAlunoControllerTest {

    @InjectMocks
    private IndicadoresAlunoController controller;

    @Mock
    private IndBioRepoImpl repoMock;

    @Mock
    private TableView<IndicadorBiomedico> tabelaMock;

    private Comum usuario;
    private Date hoje;

    @BeforeEach
    void setUp() throws Exception {
        var field = IndicadoresAlunoController.class.getDeclaredField("repo");
        field.setAccessible(true);
        field.set(controller, repoMock);

        // injeta tabela mock
        var tabelaField = IndicadoresAlunoController.class.getDeclaredField("tabelaIndicadores");
        tabelaField.setAccessible(true);
        tabelaField.set(controller, tabelaMock);

        hoje = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        usuario = new Comum ("Xavier","00000000","Xavier@gmail.com","12345",null, null, null);
    }

    // helper igual ao do seu exemplo
    private List<IndicadorBiomedico> criarListaFake() {
        return List.of(
                new IndicadorBiomedico("Xavier@gmail.com", 60.0, 1.65, 22.0, 40.0, 25.5,hoje),
                new IndicadorBiomedico("Vitoria@gmail.com", 80.0, 1.70, 30.0, 35.0, 24.4,hoje)
        );
    }

    @Test
    void testSetUsuarioChamaAtualizarTabela() {
        when(repoMock.findAll()).thenReturn(criarListaFake());

        controller.setUsuario(usuario);

        verify(repoMock).findAll();
        verify(tabelaMock).setItems(any());
    }

    @Test
    void testAtualizarTabelaFiltraPorUsuario() {
        when(repoMock.findAll()).thenReturn(criarListaFake());

        controller.setUsuario(usuario);

        var captor = org.mockito.ArgumentCaptor.forClass(ObservableList.class);
        verify(tabelaMock).setItems(captor.capture());

        ObservableList<IndicadorBiomedico> listaFinal = captor.getValue();

        assertEquals(1, listaFinal.size());
        assertEquals("Xavier@gmail.com", listaFinal.get(0).getEmail());
    }

    @Test
    void testAtualizarTabelaSemUsuarioNaoFazNada() {
        controller.setUsuario(null);

        controller.atualizarTabela();

        verify(repoMock, never()).findAll();
        verify(tabelaMock, never()).setItems(any());
    }
}
