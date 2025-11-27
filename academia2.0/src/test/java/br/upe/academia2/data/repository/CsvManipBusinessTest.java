package br.upe.academia2.data.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import br.upe.academia2.business.CSVManipBusiness;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvManipBusinessTest {

    private CSVManipBusiness csvManipBusiness;

    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        csvManipBusiness = new CSVManipBusiness();
    }


    // --- TESTES PARA O MÉTODO leitor() ---

    @Test
    @DisplayName("Deve ler as linhas de um arquivo CSV, ignorando o cabeçalho")
    void testLeitor_ComArquivoValido_DeveRetornarLinhasDeDados() throws IOException {
        // Arrange (Organização)
        // Cria um arquivo CSV temporário com conteúdo
        Path arquivoCsv = tempDir.resolve("teste.csv");
        List<String> linhasDoArquivo = Arrays.asList("cabecalho1,cabecalho2", "dado1,dadoA", "dado2,dadoB");
        Files.write(arquivoCsv, linhasDoArquivo);

        // Act (Ação)
        List<String> resultado = csvManipBusiness.leitor(arquivoCsv.toString());

        // Assert (Verificação)
        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "Deveria retornar 2 linhas de dados (sem o cabeçalho).");
        assertTrue(resultado.contains("dado1,dadoA"));
        assertTrue(resultado.contains("dado2,dadoB"));
        assertFalse(resultado.contains("cabecalho1,cabecalho2"), "Não deveria conter o cabeçalho.");
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia ao ler um arquivo CSV que só tem cabeçalho")
    void testLeitor_ComArquivoApenasComCabecalho_DeveRetornarListaVazia() throws IOException {
        // Arrange
        Path arquivoCsv = tempDir.resolve("apenas_cabecalho.csv");
        Files.write(arquivoCsv, Collections.singletonList("nome,idade"));

        // Act
        List<String> resultado = csvManipBusiness.leitor(arquivoCsv.toString());

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia se o arquivo CSV não existir")
    void testLeitor_ComArquivoInexistente_DeveRetornarListaVazia() {
        // Arrange
        String caminhoInexistente = tempDir.resolve("nao_existe.csv").toString();

        // Act
        List<String> resultado = csvManipBusiness.leitor(caminhoInexistente);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

}