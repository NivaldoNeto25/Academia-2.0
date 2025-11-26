package br.upe.academia2.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.ui.controllers.AdmMenuController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/*
 @FXML private Button btnCadastrar;
    @FXML private Button btnEditar;
    @FXML private Button btnExcluir;
    @FXML private Button btnSair;
    @FXML private TextField searchField;
    @FXML private TableView<Usuario> alunosTable;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colEmail;

 */

@ExtendWith(MockitoExtension.class)
public class AdmMenuControllerTest{

    @Spy
    @InjectMocks
    private AdmMenuController admMenuController;

    @Mock
    private Button btnCadastrar;
    @Mock
    private Button btnEditar;
    @Mock
    private Button btnExcluir;
    @Mock
    private Button btnSair;
    @Mock
    private TextField searchField;
    @Mock
    private TableView<Usuario> alunosTable;
    @Mock
    private TableColumn<Usuario, String> colNome;
    @Mock
    private TableColumn<Usuario, String> colEmail;

    @Mock
    private UsuarioBusiness usuarioBusiness;

    @BeforeAll
    public static void initJFX() {
        try {
            new javafx.embed.swing.JFXPanel();
        } catch (Exception ignored) {}
    }

    @BeforeEach
    void setUp(){
        admMenuController.setUsuarioBusiness(usuarioBusiness);
        lenient().when(searchField.textProperty()).thenReturn(new SimpleStringProperty());
        lenient().when(usuarioBusiness.listarUsuariosComuns()).thenReturn(List.of(new Comum("Vitoria", "00000000", "Vitoria@gmail.com", "12345", null, null, null), new Comum("Nivaldo","123456789", "Nivaldo@gmail.com", "123456",null,null,null)));
        lenient().when(alunosTable.getItems()).thenReturn(FXCollections.observableArrayList());
    }

    @Test
    void initializeTest(){
        admMenuController.initialize();
        verify(usuarioBusiness).listarUsuariosComuns();
        verify(alunosTable).setItems(any());
    }

    @Test
    void buscaTest(){
        var textProperty = new SimpleStringProperty();
        when(searchField.textProperty()).thenReturn(textProperty);

        admMenuController.initialize();

        textProperty.set("Vitoria");
        verify(alunosTable, atLeastOnce()).setItems(any());
    }

    @Test
    void atualizarTabelaAlunosTest(){
        admMenuController.initialize();
        admMenuController.atualizarTabelaAlunos();

        verify(usuarioBusiness, times(2)).listarUsuariosComuns();
}

    @Test
    void handleSair() throws Exception {

        Stage stageMock = mock(Stage.class);
        var sceneMock = mock(javafx.scene.Scene.class);

        when(btnSair.getScene()).thenReturn(sceneMock);
        when(sceneMock.getWindow()).thenReturn(stageMock);

        admMenuController.handleSair();

        verify(stageMock).setTitle("Academia 2.0 - Login");
        verify(stageMock).setScene(any());
    }


}
