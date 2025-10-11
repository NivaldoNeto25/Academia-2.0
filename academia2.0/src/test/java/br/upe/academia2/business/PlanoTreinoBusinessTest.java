package br.upe.academia2.business;

import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.SecaoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.interfaces.IUsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanoTreinoBusinessTest {

    // Mock para a interface do repositório de usuário
    @Mock
    private IUsuarioRepository usuarioRepository;

    // Mock para o repositório de plano de treino
    @Mock
    private PlanoTreinoCsvRepository planoRepository;

    // Injeta os mocks na classe que estamos testando
    @InjectMocks
    private PlanoTreinoBusiness planoTreinoBusiness;

    

    @Test
    @DisplayName("Deve cadastrar plano de treino com sucesso para um usuário válido")
    void testCadastrarPlanoDeTreino_ComDadosValidos_DeveChamarRepositorios() throws ParseException {
        // Arrange (Organização)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date inicio = dateFormat.parse("22/22/2222")  ;
        Date fim = dateFormat.parse("22/22/2222");
        Usuario usuario = new Comum("João", null ,"joao@upe.br", "123", null, null, null);
        PlanoTreino plano = new PlanoTreino(0, "Treino de Força", inicio, fim, usuario);

        // Act (Ação)
        planoTreinoBusiness.cadastrarPlanoDeTreino(usuario, plano);

        // Assert (Verificação)
        // Verifica se o usuário foi associado ao plano
        assertEquals(usuario, plano.getUsuario());
        // Verifica se os métodos dos repositórios foram chamados
        verify(usuarioRepository, times(1)).update(usuario);
        verify(planoRepository, times(1)).salvarPlano(plano);
    }

    @Test
    @DisplayName("Não deve cadastrar plano de treino se o usuário for nulo")
    void testCadastrarPlanoDeTreino_ComUsuarioNulo_NaoDeveFazerNada() {
        // Arrange
        PlanoTreino plano = new PlanoTreino(0, "Treino de Força", null, null, null);

        // Act
        planoTreinoBusiness.cadastrarPlanoDeTreino(null, plano);

        // Assert
        // Garante que nenhum método de repositório foi chamado
        verify(usuarioRepository, never()).update(any(Usuario.class));
        verify(planoRepository, never()).salvarPlano(any(PlanoTreino.class));
    }

    @Test
    @DisplayName("Deve carregar o plano de treino de um usuário existente")
    void testCarregarPlanoDoUsuario_QuandoPlanoExiste_DeveRetornarPlano() {
        // Arrange
        Usuario usuario = new Comum("Maria", null,"maria@upe.br", "456", null, null, null);
        PlanoTreino planoEsperado = new PlanoTreino(0, "Treino Cardio", null, null, usuario);

        // Configura o mock para retornar o plano quando o método for chamado
        when(planoRepository.carregarPlano(usuario)).thenReturn(planoEsperado);

        // Act
        PlanoTreino planoCarregado = planoTreinoBusiness.carregarPlanoDoUsuario(usuario);

        // Assert
        assertNotNull(planoCarregado);
        assertEquals("Treino Cardio", planoCarregado.getNomePlano());
        // Verifica se o usuário foi corretamente associado ao plano carregado
        assertEquals(usuario, planoCarregado.getUsuario());
        verify(planoRepository, times(1)).carregarPlano(usuario);
    }

    @Test
    @DisplayName("Deve retornar nulo ao carregar plano de um usuário que não tem plano")
    void testCarregarPlanoDoUsuario_QuandoPlanoNaoExiste_DeveRetornarNulo() {
        // Arrange
        Usuario usuario = new Comum("Pedro", null ,"pedro@upe.br", "789", null, null, null);
        when(planoRepository.carregarPlano(usuario)).thenReturn(null);

        // Act
        PlanoTreino planoCarregado = planoTreinoBusiness.carregarPlanoDoUsuario(usuario);

        // Assert
        assertNull(planoCarregado);
    }

    @Test
    @DisplayName("Deve chamar o método de salvar ao modificar um plano de treino válido")
    void testModificarPlanoDeTreino_ComPlanoValido_DeveChamarSalvarPlano() throws ParseException {
        // Arrange
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date inicio = dateFormat.parse("22/22/2222")  ;
        Date fim = dateFormat.parse("22/22/2222");
        Usuario usuario = new Comum("Ana", null ,"ana@upe.br", "abc", null, null, null);
        PlanoTreino plano = new PlanoTreino(0, "Treino Hipertrofia", inicio, fim, usuario);
        plano.setUsuario(usuario); // Associa o usuário ao plano

        // Act
        planoTreinoBusiness.modificarPlanoDeTreino(plano);

        // Assert
        verify(planoRepository, times(1)).salvarPlano(plano);
    }

    @Test
    @DisplayName("Não deve chamar o método de salvar se o plano a ser modificado for nulo")
    void testModificarPlanoDeTreino_ComPlanoNulo_NaoDeveChamarSalvarPlano() {
        // Act
        planoTreinoBusiness.modificarPlanoDeTreino(null);

        // Assert
        verify(planoRepository, never()).salvarPlano(any(PlanoTreino.class));
    }

    @Test
    @DisplayName("Deve exibir o plano de treino sem lançar exceções")
    void testExibirPlanoDeTreino_ComPlanoCompleto_NaoDeveLancarExcecao() {
        // Arrange
        // Cria um plano de treino completo com seções e itens
        PlanoTreino plano = new PlanoTreino(0, "Plano Completo", null, null, null);
        SecaoTreino secaoA = new SecaoTreino("Peito e Tríceps", "Supino Reto", plano);
        Exercicio supino = new Exercicio("Supino Reto", "Peitoral", "");
        ItemPlanoTreino itemSupino = new ItemPlanoTreino(supino, 3, 10, 80);
        secaoA.setItensPlano(List.of(itemSupino));
        plano.setSecoes(new ArrayList<>(List.of(secaoA)));

        // Act & Assert
        // Como o método só usa logger, o teste garante que ele roda sem erros.
        assertDoesNotThrow(() -> planoTreinoBusiness.exibirPlanoDeTreino(plano));
    }
}