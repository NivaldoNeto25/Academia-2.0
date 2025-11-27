package br.upe.academia2.business;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CSVManipBusiness {
    private static final Logger logger = Logger.getLogger(CSVManipBusiness.class.getName());

    public List<String> leitor(String caminho) {
        List<String> resposta = new ArrayList<>();
        try (BufferedReader leitor = new BufferedReader(new FileReader(caminho, StandardCharsets.UTF_8))) {
            String linha;
            boolean primeiraLinha = true;
            while ((linha = leitor.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false; // Pular cabeçalho se houver
                    continue;
                }
                resposta.add(linha);
            }
        } catch (IOException e) {
            logger.severe("Erro ao ler o arquivo CSV: " + e.getMessage());
        }
        return resposta;
    }
}
