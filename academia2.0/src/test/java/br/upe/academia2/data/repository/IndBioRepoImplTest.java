package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import org.junit.jupiter.api.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IndBioRepoImplTest {

    private IndBioJpaRepository indBioRepository;

    @BeforeEach
    void setUp() {
        indBioRepository = new IndBioJpaRepository();
        
    
    }

    @Test
    void testSaveIndicadorBiomedico() {
        IndicadorBiomedico indicador = new IndicadorBiomedico(
                70.0, 1.75, 15.0, 40.0, 22.86, new Date()
        );
        

        boolean result = indBioRepository.save(indicador);
        
        assertTrue(result);

        List<IndicadorBiomedico> allIndicators = indBioRepository.findAll();
        assertNotNull(allIndicators);
        assertFalse(allIndicators.isEmpty());
    }

    @Test
    void testSaveNullIndicadorBiomedico() {
        // No JPA, tentar persistir 'null' lança uma IllegalArgumentException,
        // a menos que você tenha um try-catch explicito no Repository retornando false.
        // Como o seu código não tem try-catch, o teste correto é verificar a Exceção.
        assertThrows(IllegalArgumentException.class, () -> {
            indBioRepository.save(null);
        });
    }

    @Test
    void testFindAll() {
        
        List<IndicadorBiomedico> allIndicators = indBioRepository.findAll();
        assertNotNull(allIndicators);
    }

    @Test
    void testFindAllWithMultipleIndicators() {
        // Criação de objetos
        IndicadorBiomedico indicador1 = new IndicadorBiomedico(
                 70.0, 1.75, 15.0, 40.0, 22.86, new Date()
        );
        IndicadorBiomedico indicador2 = new IndicadorBiomedico(
                 75.0, 1.80, 18.0, 42.0, 23.15, new Date()
        );

        // Salva no banco
        indBioRepository.save(indicador1);
        indBioRepository.save(indicador2);

        // Verifica se salvou
        List<IndicadorBiomedico> allIndicators = indBioRepository.findAll();
        assertNotNull(allIndicators);
        assertTrue(allIndicators.size() >= 2);
    }
}