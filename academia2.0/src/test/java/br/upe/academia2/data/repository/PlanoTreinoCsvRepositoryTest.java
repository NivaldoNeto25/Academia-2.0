package br.upe.academia2.data.repository;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanoTreinoCsvRepositoryTest {

    @Mock
    private Usuario mockUsuario;
    @Mock
    private ExercicioBusiness mockExercicioBusiness;

    // JUnit 5 vai criar e limpar um diretório temporário para nós
    @TempDir
    Path tempDir;

    private PlanoTreinoJpaRepository repository;
    private String testBaseDir;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        // Configura o repositório para usar o diretório de teste e o business mockado
        testBaseDir = tempDir.toString() + File.separator;
        repository = new PlanoTreinoJpaRepository(testBaseDir, mockExercicioBusiness);

        // Configuração padrão do mock de usuário
        when(mockUsuario.getEmail()).thenReturn("test@user.com");
    }

    @Test
    void testSalvarPlanos_DeveEscreverArquivoCorretamente() throws Exception {
        // --- Arrange (Preparação) ---
        
        Exercicio ex1 = new Exercicio("Supino Reto", "Peito", null);
        Exercicio ex2 = new Exercicio("Agachamento", "Perna", null);
        ItemPlanoTreino item1 = new ItemPlanoTreino(ex1, 3, 12, 50);
        ItemPlanoTreino item2 = new ItemPlanoTreino(ex2, 4, 10, 80);

        SecaoTreino secaoA = new SecaoTreino("sec1", "Peito, Ombros", null); // Testa o escape de vírgula
        secaoA.addItemSecao(item1);
        SecaoTreino secaoB = new SecaoTreino("sec2", "Pernas", null);
        secaoB.addItemSecao(item2);

        Date data = dateFormat.parse("2025-01-01 12:00:00");
        PlanoTreino plano1 = new PlanoTreino(1, "Plano Força", data, data, mockUsuario);
        plano1.setSecoes(List.of(secaoA, secaoB));

        List<PlanoTreino> planosParaSalvar = List.of(plano1);

        // --- Act (Ação) ---
        repository.salvarPlanos(planosParaSalvar, mockUsuario);

        // --- Assert (Verificação) ---
        // 1. Verificar se o arquivo foi criado
        File arquivoSalvo = new File(testBaseDir + "plano_test_user_com.csv");
        assertTrue(arquivoSalvo.exists());

        // 2. Verificar o conteúdo do arquivo
        List<String> linhas = Files.readAllLines(arquivoSalvo.toPath());
        
        
        assertEquals(5, linhas.size()); 
        
        assertEquals("id,nomePlano,inicioPlano,fimPlano,emailUsuario", linhas.get(0));
        assertEquals("1,Plano Força,2025-01-01 12:00:00,2025-01-01 12:00:00,test@user.com", linhas.get(1));
        
        // CORREÇÃO 2: Esperar a string escapada
        assertEquals("Peito\\, Ombros,Supino Reto,3,12,50", linhas.get(2)); 
        
        assertEquals("Pernas,Agachamento,4,10,80", linhas.get(3));
        assertEquals("PLANO_END", linhas.get(4));
        
        
    }

    @Test
    void testCarregarPlanos_DeveLerArquivoCorretamente() throws Exception {
        // --- Arrange (Preparação) ---
        // 1. Mocar as dependências
        Exercicio ex1 = new Exercicio("Supino Reto", "Peito", null);
        Exercicio ex2 = new Exercicio("Barra Fixa", "Costas", null);
        when(mockExercicioBusiness.buscarExercicioPorNome("Supino Reto")).thenReturn(ex1);
        when(mockExercicioBusiness.buscarExercicioPorNome("Barra Fixa")).thenReturn(ex2);

        // 2. Criar o arquivo CSV falso
        String dateStr = "2025-10-23 00:00:00";
        Date expectedDate = dateFormat.parse(dateStr);
        String conteudoCsv = "id,nomePlano,inicioPlano,fimPlano,emailUsuario\n" +
                             "1,Plano A," + dateStr + "," + dateStr + ",test@user.com\n" +
                             "Peito,Supino Reto,3,10,70\n" +
                             "PLANO_END\n" +
                             "2,Plano B," + dateStr + "," + dateStr + ",test@user.com\n" +
                             "Costas,Barra Fixa,4,8,0\n" +
                             "PLANO_END\n";
        
        File arquivoPlano = new File(testBaseDir + "plano_test_user_com.csv");
        Files.writeString(arquivoPlano.toPath(), conteudoCsv);

        // --- Act (Ação) ---
        List<PlanoTreino> planosCarregados = repository.carregarPlanos(mockUsuario);

        // --- Assert (Verificação) ---
        assertNotNull(planosCarregados);
        assertEquals(2, planosCarregados.size());

        // Verifica Plano 1
        PlanoTreino plano1 = planosCarregados.get(0);
        assertEquals(1, plano1.getId());
        assertEquals("Plano A", plano1.getNomePlano());
        assertEquals(expectedDate, plano1.getInicioPlano());
        assertEquals(mockUsuario, plano1.getUsuario());
        assertEquals(1, plano1.getSecoes().size());
        SecaoTreino secao1 = plano1.getSecaoPorNome("Peito");
        assertNotNull(secao1);
        assertEquals("Supino Reto", secao1.getItensPlano().get(0).getExercicio().getNome());
        assertEquals(3, secao1.getItensPlano().get(0).getSeries());
        assertEquals(10, secao1.getItensPlano().get(0).getRepeticoes());

        // Verifica Plano 2
        PlanoTreino plano2 = planosCarregados.get(1);
        assertEquals(2, plano2.getId());
        assertEquals("Plano B", plano2.getNomePlano());
        assertEquals(1, plano2.getSecoes().size());
        SecaoTreino secao2 = plano2.getSecaoPorNome("Costas");
        assertNotNull(secao2);
        assertEquals("Barra Fixa", secao2.getItensPlano().get(0).getExercicio().getNome());
        assertEquals(4, secao2.getItensPlano().get(0).getSeries());
        assertEquals(8, secao2.getItensPlano().get(0).getRepeticoes());
    }

    @Test
    void testCarregarPlanos_ArquivoNaoExiste_DeveRetornarListaVazia() {
        // --- Act ---
        // Nenhum arquivo foi criado
        List<PlanoTreino> planosCarregados = repository.carregarPlanos(mockUsuario);

        // --- Assert ---
        assertNotNull(planosCarregados);
        assertTrue(planosCarregados.isEmpty());
    }

    @Test
    void testCarregarPlanos_ExercicioNaoEncontrado_DeveIgnorarItem() throws Exception {
        // --- Arrange ---
        // "Exercicio Fantasma" não será mockado
        Exercicio ex1 = new Exercicio("Supino Reto", "Peito", null);
        when(mockExercicioBusiness.buscarExercicioPorNome("Supino Reto")).thenReturn(ex1);
        when(mockExercicioBusiness.buscarExercicioPorNome("Exercicio Fantasma")).thenReturn(null);

        String dateStr = "2025-10-23 00:00:00";
        String conteudoCsv = "id,nomePlano,inicioPlano,fimPlano,emailUsuario\n" +
                             "1,Plano A," + dateStr + "," + dateStr + ",test@user.com\n" +
                             "Peito,Supino Reto,3,10,70\n" +
                             "Ombros,Exercicio Fantasma,3,15,10\n" + // Este item será ignorado
                             "PLANO_END\n";
        
        File arquivoPlano = new File(testBaseDir + "plano_test_user_com.csv");
        Files.writeString(arquivoPlano.toPath(), conteudoCsv);

        // --- Act ---
        List<PlanoTreino> planosCarregados = repository.carregarPlanos(mockUsuario);

        // --- Assert ---
        assertEquals(1, planosCarregados.size());
        PlanoTreino plano = planosCarregados.get(0);
        assertEquals(2, plano.getSecoes().size()); // A seção "Ombros" é criada

        SecaoTreino secaoPeito = plano.getSecaoPorNome("Peito");
        SecaoTreino secaoOmbros = plano.getSecaoPorNome("Ombros");

        assertNotNull(secaoPeito);
        assertNotNull(secaoOmbros);
        
        assertEquals(1, secaoPeito.getItensPlano().size()); // Item do Supino foi adicionado
        assertTrue(secaoOmbros.getItensPlano().isEmpty()); // Item "Fantasma" foi ignorado
    }
}