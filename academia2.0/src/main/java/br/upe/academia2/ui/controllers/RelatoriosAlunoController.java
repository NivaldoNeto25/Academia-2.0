package br.upe.academia2.ui.controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

import br.upe.academia2.business.CSVManipBusiness;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class RelatoriosAlunoController {

    private static final Logger logger = Logger.getLogger(RelatoriosAlunoController.class.getName());
    private Usuario usuario;

    @FXML
    private TextArea txtSaida;

    private static final String caminhoCSV = "data/indicadores.csv";
    private final CSVManipBusiness csvManip = new CSVManipBusiness();
    private static final String FORMATO_DATA = "yyyy-MM-dd HH:mm:ss";
    
    private Stage stageAnterior; // Janela do AlunoMenuController

    @FXML
    private Button btnVoltar;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }
    
    // ===========================
    // BOTÃO RELATÓRIO GERAL
    // ===========================
    @FXML
    private void handleRelatorioGeral(ActionEvent event) {
        if (usuario == null) {
            txtSaida.setText(" Usuário não definido! Faça login novamente.");
            return;
        }

        txtSaida.clear();
        txtSaida.appendText(" Gerando Relatório Geral...\n");

        List<IndicadorBiomedico> lista = carregarIndicadores(usuario.getEmail());
        if (lista.isEmpty()) {
            txtSaida.appendText("Nenhum indicador encontrado para este usuário.\n");
            return;
        }

        List<List<String>> dadosParaCSV = new ArrayList<>();
        for (IndicadorBiomedico ind : lista) {
            txtSaida.appendText(formatarIndicador(ind));
            dadosParaCSV.add(Arrays.asList(
                    ind.getEmail(),
                    String.format(Locale.US, "%.2f", ind.getPeso()),
                    String.format(Locale.US, "%.2f", ind.getAltura()),
                    String.format(Locale.US, "%.2f", ind.getPercentualGordura()),
                    String.format(Locale.US, "%.2f", ind.getPercentualMassaMagra()),
                    String.format(Locale.US, "%.2f", ind.getImc()),
                    new SimpleDateFormat(FORMATO_DATA).format(ind.getDataRegistro())
            ));
        }

        salvarCSV("relatorio_geral_" + usuario.getEmail().replace("@", "_") + ".csv",
                Arrays.asList("Email", "Peso", "Altura", "Gordura", "Massa Magra", "IMC", "Data Registro"),
                dadosParaCSV);

        txtSaida.appendText("\n Relatório Geral gerado com sucesso!\n");
    }

    // ===========================
    // BOTÃO RELATÓRIO COMPARATIVO
    // ===========================
    @FXML
    private void handleRelatorioComparativo(ActionEvent event) {
        if (usuario == null) {
            txtSaida.setText(" Usuário não definido! Faça login novamente.");
            return;
        }

        txtSaida.clear();
        txtSaida.appendText(" Gerando Relatório Comparativo...\n");

        List<IndicadorBiomedico> lista = carregarIndicadores(usuario.getEmail());
        if (lista.isEmpty()) {
            txtSaida.appendText("Nenhum indicador encontrado.\n");
            return;
        }

        if (lista.size() < 2) {
            txtSaida.appendText("Apenas um registro encontrado. Exibindo:\n");
            txtSaida.appendText(formatarIndicador(lista.get(0)));
            return;
        }

        // Pega os dois registros mais recentes
        lista.sort(Comparator.comparing(IndicadorBiomedico::getDataRegistro).reversed());
        IndicadorBiomedico atual = lista.get(0);
        IndicadorBiomedico anterior = lista.get(1);

        txtSaida.appendText(" Comparando os dois registros mais recentes:\n\n");
        txtSaida.appendText(" Registro mais recente:\n" + formatarIndicador(atual) + "\n");
        txtSaida.appendText(" Registro anterior:\n" + formatarIndicador(anterior));

        List<List<String>> dados = List.of(
                Arrays.asList("Atual",
                        String.format(Locale.US, "%.2f", atual.getPeso()),
                        String.format(Locale.US, "%.2f", atual.getAltura()),
                        String.format(Locale.US, "%.2f", atual.getPercentualGordura()),
                        String.format(Locale.US, "%.2f", atual.getPercentualMassaMagra()),
                        String.format(Locale.US, "%.2f", atual.getImc()),
                        new SimpleDateFormat(FORMATO_DATA).format(atual.getDataRegistro())),
                Arrays.asList("Anterior",
                        String.format(Locale.US, "%.2f", anterior.getPeso()),
                        String.format(Locale.US, "%.2f", anterior.getAltura()),
                        String.format(Locale.US, "%.2f", anterior.getPercentualGordura()),
                        String.format(Locale.US, "%.2f", anterior.getPercentualMassaMagra()),
                        String.format(Locale.US, "%.2f", anterior.getImc()),
                        new SimpleDateFormat(FORMATO_DATA).format(anterior.getDataRegistro()))
        );

        salvarCSV("relatorio_comparativo_" + usuario.getEmail().replace("@", "_") + ".csv",
                Arrays.asList("Registro", "Peso", "Altura", "Gordura", "Massa Magra", "IMC", "Data Registro"),
                dados);

        txtSaida.appendText("\n Relatório Comparativo gerado com sucesso!\n");
    }


    @FXML
    private void handleVoltar() throws IOException {
           URL fxml = Objects.requireNonNull(
            getClass().getResource("/fxml/AlunoMenu.fxml"),
            "FXML /fxml/AlunoMenu.fxml não encontrado"
        );
        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root = loader.load();

        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        Stage nova = new Stage();
        nova.setScene(new Scene(root));
        atual.close();
        nova.show();
    }
    
    // ===========================
    // MÉTODOS AUXILIARES
    // ===========================

    private List<IndicadorBiomedico> carregarIndicadores(String emailUsuario) {
        List<IndicadorBiomedico> resultado = new ArrayList<>();
        try {
            List<String> linhas = csvManip.leitor(caminhoCSV);
            for (String linha : linhas) {
                String[] dados = linha.split(",");
                if (dados.length >= 7 && dados[0].equals(emailUsuario)) {
                    String email = dados[0];
                    double peso = Double.parseDouble(dados[1]);
                    double altura = Double.parseDouble(dados[2]);
                    double gordura = Double.parseDouble(dados[3]);
                    double massa = Double.parseDouble(dados[4]);
                    double imc = Double.parseDouble(dados[5]);
                    Date data = new SimpleDateFormat(FORMATO_DATA).parse(dados[6]);
                    resultado.add(new IndicadorBiomedico(email, peso, altura, gordura, massa, imc, data));
                }
            }
        } catch (Exception e) {
            txtSaida.appendText("Erro ao carregar dados: " + e.getMessage() + "\n");
        }
        return resultado;
    }

    private void salvarCSV(String nomeArquivo, List<String> cabecalho, List<List<String>> linhas) {
        try (FileWriter writer = new FileWriter("data/" + nomeArquivo)) {
            writer.append(String.join(",", cabecalho)).append("\n");
            for (List<String> linha : linhas) {
                writer.append(String.join(",", linha)).append("\n");
            }
            txtSaida.appendText("\n Arquivo salvo em: data/" + nomeArquivo + "\n");
        } catch (Exception e) {
            txtSaida.appendText("Erro ao salvar CSV: " + e.getMessage() + "\n");
        }
    }

    private String formatarIndicador(IndicadorBiomedico ind) {
        return String.format(
                """
                ------------------------------
                Data: %s
                Peso: %.2f kg
                Altura: %.2f m
                IMC: %.2f
                Gordura: %.2f%%
                Massa Magra: %.2f%%
                ------------------------------
                """,
                new SimpleDateFormat(FORMATO_DATA).format(ind.getDataRegistro()),
                ind.getPeso(),
                ind.getAltura(),
                ind.getImc(),
                ind.getPercentualGordura(),
                ind.getPercentualMassaMagra()
        );
    }


}
