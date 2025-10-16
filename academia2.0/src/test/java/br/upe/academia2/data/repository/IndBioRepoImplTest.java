package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IndBioRepoImplTest {

    private IndBioRepoImpl indBioRepository;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("indicadores-test", ".csv");
        tempFile.deleteOnExit();

        indBioRepository = new IndBioRepoImpl(tempFile.getAbsolutePath());

        indBioRepository.limparDados();
    }

    @Test
    void testSaveIndicadorBiomedico() {
        IndicadorBiomedico indicador = new IndicadorBiomedico(
                "teste@email.com", 70.0, 1.75, 15.0, 40.0, 22.86, new Date()
        );

        boolean result = indBioRepository.save(indicador);
        assertTrue(result);

        List<IndicadorBiomedico> allIndicators = indBioRepository.findAll();
        assertNotNull(allIndicators);
        assertEquals(1, allIndicators.size());
        assertEquals(indicador.getEmail(), allIndicators.get(0).getEmail());
    }

    @Test
    void testSaveNullIndicadorBiomedico() {
        boolean result = indBioRepository.save(null);
        assertFalse(result);

        List<IndicadorBiomedico> allIndicators = indBioRepository.findAll();
        assertNotNull(allIndicators);
        assertTrue(allIndicators.isEmpty());
    }

    @Test
    void testFindAllEmpty() {
        List<IndicadorBiomedico> allIndicators = indBioRepository.findAll();
        assertNotNull(allIndicators);
        assertTrue(allIndicators.isEmpty());
    }

    @Test
    void testFindAllWithMultipleIndicators() {
        IndicadorBiomedico indicador1 = new IndicadorBiomedico(
                "teste1@email.com", 70.0, 1.75, 15.0, 40.0, 22.86, new Date()
        );
        IndicadorBiomedico indicador2 = new IndicadorBiomedico(
                "teste2@email.com", 75.0, 1.80, 18.0, 42.0, 23.15, new Date()
        );
        IndicadorBiomedico indicador3 = new IndicadorBiomedico(
                "teste3@email.com", 65.0, 1.70, 12.0, 38.0, 22.49, new Date()
        );

        indBioRepository.save(indicador1);
        indBioRepository.save(indicador2);
        indBioRepository.save(indicador3);

        List<IndicadorBiomedico> allIndicators = indBioRepository.findAll();

        assertNotNull(allIndicators);
        assertEquals(3, allIndicators.size());
    }
}
