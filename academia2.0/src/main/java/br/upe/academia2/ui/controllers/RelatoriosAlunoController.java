package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.ui.Relatorios;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class RelatoriosAlunoController extends JFrame {

    private final Relatorios relatorios = new Relatorios();
    private final JTextField emailField;
    private final JTextArea outputArea;

    public RelatoriosAlunoController() {
        setTitle("Relatórios de Aluno - Academia");
        setSize(650, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Painel superior
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Email do aluno:"));
        emailField = new JTextField(25);
        topPanel.add(emailField);
        add(topPanel, BorderLayout.NORTH);

        // Área de texto
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton geralButton = new JButton("Gerar Relatório Geral");
        JButton comparativoButton = new JButton("Gerar Relatório Comparativo");
        JButton abrirPastaButton = new JButton("Abrir Pasta dos Relatórios");

        buttonPanel.add(geralButton);
        buttonPanel.add(comparativoButton);
        buttonPanel.add(abrirPastaButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Ações dos botões
        geralButton.addActionListener(e -> gerarRelatorioGeral());
        comparativoButton.addActionListener(e -> gerarRelatorioComparativo());
        abrirPastaButton.addActionListener(e -> abrirPastaRelatorios());
    }

    private void gerarRelatorioGeral() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o email do aluno.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setEmail(email);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(buffer);
            PrintStream oldOut = System.out;
            System.setOut(printStream);

            relatorios.relatorioGeral(usuario);

            System.out.flush();
            System.setOut(oldOut);
            outputArea.setText(buffer.toString());

            JOptionPane.showMessageDialog(this, "Relatório geral gerado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarRelatorioComparativo() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o email do aluno.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setEmail(email);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(buffer);
            PrintStream oldOut = System.out;
            System.setOut(printStream);

            relatorios.relatorioComparativo(usuario);

            System.out.flush();
            System.setOut(oldOut);
            outputArea.setText(buffer.toString());

            JOptionPane.showMessageDialog(this, "Relatório comparativo gerado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirPastaRelatorios() {
        File pasta = new File("data");

        if (!pasta.exists()) {
            JOptionPane.showMessageDialog(this, "A pasta 'data/' ainda não existe. Gere um relatório primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(pasta);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir a pasta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RelatoriosAlunoController().setVisible(true));
    }
}
