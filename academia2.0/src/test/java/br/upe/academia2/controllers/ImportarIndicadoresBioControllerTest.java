package br.upe.academia2.controllers;

import br.upe.academia2.business.IndicadorBiomedicoBusiness;
import br.upe.academia2.ui.controllers.ImportarIndicadoresBioController;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportarIndicadoresBioControllerTest {

    @InjectMocks
    private ImportarIndicadoresBioController controller;

    @Mock
    private IndicadorBiomedicoBusiness indicadorBusiness;

    private TextField campoArquivo;
    private Label mensagemLabel;

    @BeforeEach
    void setUp() throws Exception {

        campoArquivo = new TextField();
        mensagemLabel = new Label();

        // injet campo de arquivo
        Field f1 = ImportarIndicadoresBioController.class.getDeclaredField("campoArquivo");
        f1.setAccessible(true);
        f1.set(controller, campoArquivo);

        // mengem label
        Field f2 = ImportarIndicadoresBioController.class.getDeclaredField("mensagemLabel");
        f2.setAccessible(true);
        f2.set(controller, mensagemLabel);

        // mock de IndicadorBiomedicoBusiness
        Field f3 = ImportarIndicadoresBioController.class.getDeclaredField("indicadorBusiness");
        f3.setAccessible(true);
        f3.set(controller, indicadorBusiness);
    }

    @Test
    void testArquivoVazio() {

        campoArquivo.setText("");

        controller.handleImportar();

        assertEquals("Selecione um arquivo CSV válido.", mensagemLabel.getText());
        verify(indicadorBusiness, never()).importarIndicadoresDeCSV(anyString());
    }

    @Test
    void testImportacaoComSucesso() {

        campoArquivo.setText("dados.csv");

        when(indicadorBusiness.importarIndicadoresDeCSV("dados.csv"))
                .thenReturn(true);

        controller.handleImportar();

        assertEquals("Importação realizada com sucesso!", mensagemLabel.getText());
        verify(indicadorBusiness).importarIndicadoresDeCSV("dados.csv");
    }

    @Test
    void testImportacaoComFalha() {

        campoArquivo.setText("dados.csv");

        when(indicadorBusiness.importarIndicadoresDeCSV("dados.csv"))
                .thenReturn(false);

        controller.handleImportar();

        assertEquals("Erro ao importar dados. Verifique o arquivo.", mensagemLabel.getText());
        verify(indicadorBusiness).importarIndicadoresDeCSV("dados.csv");
    }
    @Test
    void testErroAoImportar() {

        campoArquivo.setText("arquivo.csv");

        when(indicadorBusiness.importarIndicadoresDeCSV("arquivo.csv"))
                .thenThrow(new RuntimeException("Falha inesperada"));

        try {
            controller.handleImportar();
        } catch (Exception ignored) {}

        verify(indicadorBusiness).importarIndicadoresDeCSV("arquivo.csv");
    }
}

