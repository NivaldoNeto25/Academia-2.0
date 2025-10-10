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

        try(BufferedReader leitor = new BufferedReader(new FileReader(caminho));) {
            String linha;
            boolean primeiraLinha = true;
            while ((linha = leitor.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }
                String[] vector = linha.split("\n");
                String instancia = vector[0];
                resposta.add(instancia);
            }
        } catch (IOException e) {
            logger.severe("Erro ao ler o arquivo CSV: " + e.getMessage());
        }
        return resposta;
    }
    public void escritor(List<String> nomeDosCampos, List<String> input, String nomeDoArquivo, String caminhoDoArquivo){
        String caminhoAbsoluto = caminhoDoArquivo + "/" + nomeDoArquivo;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoAbsoluto, StandardCharsets.UTF_8,true))) {
            File directory = new File(caminhoAbsoluto);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            for (int index = 0; index < input.size(); index++) {
                if(index == 0){
                    for(int ind = 0; ind < nomeDosCampos.size();ind++){
                        writer.write(nomeDosCampos.get(ind));
                        if(ind != nomeDosCampos.size()-1){
                            writer.write(",");
                        }
                        writer.write("\n");
                    }
                }else {
                    writer.write(input.get(index) + "\n");
                }
            }
            writer.flush();
        }catch (IOException e){
            logger.severe("Erro ao escrever no arquivo CSV: " + e.getMessage());    
        }
    }
}