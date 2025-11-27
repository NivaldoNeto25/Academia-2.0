package br.upe.academia2.controllers;

import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.repository.IndBioJpaRepository; // Import do Repositório
import br.upe.academia2.ui.controllers.IndicadoresAlunoController;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadoresAlunoControllerTest {

    @InjectMocks
    private IndicadoresAlunoController controller;

    @Mock
    private IndBioJpaRepository repoMock; 

    @Mock
    private TableView<IndicadorBiomedico> tabelaMock;

    private Comum usuario;

    @BeforeEach
    void setUp() throws Exception {
        // Tenta injetar no campo 'repo' (conforme indicado pelo erro do log)
        Field field = IndicadoresAlunoController.class.getDeclaredField("repo");
        field.setAccessible(true);
        field.set(controller, repoMock); // Injeta o mock do repositório

        // Injeta tabela mock
        Field tabelaField = IndicadoresAlunoController.class.getDeclaredField("tabelaIndicadores");
        tabelaField.setAccessible(true);
        tabelaField.set(controller, tabelaMock);

        usuario = new Comum("Xavier", "00", "email@test.com", "123", null, null, null);
    }

    @Test
    void testSetUsuarioChamaRepoEAtualizaTabela() {
        IndicadorBiomedico ind = new IndicadorBiomedico();
        ind.setUsuario(usuario);
        
        when(repoMock.findAll()).thenReturn(List.of(ind));

        controller.setUsuario(usuario);

        verify(repoMock).findAll(); // Verifica se chamou o banco
        verify(tabelaMock).setItems(any()); // Verifica se tentou atualizar a tabela
    }
    
    @Test
    void testSetUsuarioListaVazia() {
        when(repoMock.findAll()).thenReturn(Collections.emptyList());

        controller.setUsuario(usuario);

        verify(repoMock).findAll();
        verify(tabelaMock).setItems(any()); 
    }
}