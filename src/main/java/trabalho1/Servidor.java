package trabalho1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
    private static Map<String, Pessoa> pessoas = new HashMap<>();
    private static Map<Long, Equipe> equipes = new HashMap<>();

    public static void main(String[] args) {
        // Inicializar o servidor com dados predefinidos
        inicializarDados();
        
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor iniciado...");

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    // Lê e processa uma única mensagem do cliente
                    String message = in.readLine();
                    String response = processarMensagem(message);
                    out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Método para inicializar dados predefinidos
    private static void inicializarDados() {
        // Inserir alguns administradores
        Administrador adm1 = new Administrador("111.111.111-11", "Adm1", "Endereço Adm1", "Setor A");
        Administrador adm2 = new Administrador("222.222.222-22", "Adm2", "Endereço Adm2", "Setor B");
        Administrador adm3 = new Administrador("666.666.666-66", "Adm3", "Endereço Adm3", "Setor C");
        pessoas.put(adm1.getCpf(), adm1);
        pessoas.put(adm2.getCpf(), adm2);
        pessoas.put(adm3.getCpf(), adm3);

        // Inserir alguns membros
        Membro membro1 = new Membro("333.333.333-33", "Membro1", "Endereço Membro1", LocalDate.now(), LocalTime.now());
        Membro membro2 = new Membro("444.444.444-44", "Membro2", "Endereço Membro2", LocalDate.now(), LocalTime.now());
        Membro membro3 = new Membro("555.555.555-55", "Membro3", "Endereço Membro3", null, null);
        pessoas.put(membro1.getCpf(), membro1);
        pessoas.put(membro2.getCpf(), membro2);
        pessoas.put(membro3.getCpf(), membro3);

        // Criar uma equipe associando um administrador e um membro
        Equipe equipe1 = new Equipe("Equipe 1", adm1, membro1);
        Equipe equipe2 = new Equipe("Equipe 2", adm2, membro2);
        equipes.put(equipe1.getId(), equipe1);
        equipes.put(equipe2.getId(), equipe2);
    }

    private static String processarMensagem(String message) {
        String[] parts = message.split(";");
        String operation = parts[0];

        switch (operation) {
            case "INSERT":
                return inserirPessoa(parts);
            case "UPDATE":
                return atualizarPessoa(parts);
            case "GET":
                return obterPessoa(parts[1]);
            case "DELETE":
                return removerPessoa(parts[1]);
            case "LIST":
                return listarPessoas();
            case "INSERT_EQUIPE":
                return inserirEquipe(parts);
            case "GET_EQUIPE":
                return obterEquipe(parts[1]);
            case "LIST_EQUIPES":
                return obterEquipes();
            case "INSERT_MEMBRO_EQUIPE":
                return inserirMembroEquipe(parts);
            case "DELETE_EQUIPE":
                return removerEquipe(parts);
            default:
                return "Operação inválida";
        }
    }

    // Inserir um adm/membro. (INSERT;cpf;nome;endereco) = MEMBRO; (INSERT;cpf;nome;endereco;setor) = ADM 
    private static String inserirPessoa(String[] parts) {
        try {
            if (parts.length < 4) {
                return "Dados insuficientes para inserir pessoa";
            }

            String cpf = parts[1];
            
            // Verificar se o CPF já está cadastrado
            if (pessoas.containsKey(cpf)) {
                return "CPF já cadastrado!";
            }
            
            String nome = parts[2];
            String endereco = parts[3];
            
            if (parts.length == 5) { // Administrador
                String setorResponsavel = parts[4];
                Administrador administrador = new Administrador(cpf, nome, endereco, setorResponsavel);
                pessoas.put(cpf, administrador);
                return "Administrador inserido com sucesso.";
            } else if (parts.length == 4) { // Membro
                Membro membro = new Membro(cpf, nome, endereco, null, null);
                pessoas.put(cpf, membro);
                return "Membro inserido com sucesso.";
            } else {
                return "Formato de dados inválido para inserção";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao inserir pessoa";
        }
    }

    // Alterar um adm/membro. (UPDATE;cpf;nome;endereco)
    private static String atualizarPessoa(String[] parts) {
        try {
            if (parts.length < 4) {
                return "Dados insuficientes para atualizar pessoa";
            }
            
            String cpf = parts[1];
            String nome = parts[2];
            String endereco = parts[3];

            Pessoa pessoa = pessoas.get(cpf);
            if (pessoa != null) {
                pessoa.setNome(nome);
                pessoa.setEndereco(endereco);
                return pessoa instanceof Administrador ? "Administrador(a) " + pessoa.getNome() + " atualizado com sucesso." : "Membro " + pessoa.getNome() + " atualizado com sucesso.";
            } else {
                return "Pessoa não encontrada";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao atualizar pessoa: " + e.getMessage();
        }
    }

    // Obter um adm/membro. (GET;cpf)
    private static String obterPessoa(String cpf) {
        try {
            Pessoa pessoa = pessoas.get(cpf);
            if (pessoa != null) {
                return pessoa.toString();
            } else {
                return "Pessoa não encontrada";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao obter pessoa: " + e.getMessage();
        }
    }

    // Remover um adm/membro. (DELETE;cpf)
    private static String removerPessoa(String cpf) {
        try {
            Pessoa pessoa = pessoas.get(cpf);

            if (pessoa != null) {
                // Verificar se a pessoa está associada a alguma equipe
                for (Equipe equipe : equipes.values()) {
                    if ((pessoa instanceof Administrador && equipe.getAdministrador().equals(pessoa)) ||
                        (pessoa instanceof Membro && equipe.getMembros().contains(pessoa))) {

                        return "Pessoa não pode ser removida porque está associada à equipe: " + equipe.getId() + " - " + equipe.getNome();
                    }
                }

                // Se a pessoa não está associada a nenhuma equipe, pode ser removida
                pessoas.remove(cpf);
                return pessoa instanceof Administrador ? "Administrador(a) " + pessoa.getNome() + " removido(a) com sucesso." : "Membro " + pessoa.getNome() + " removido(a) com sucesso.";
            } else {
                return "Pessoa não encontrada";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao remover pessoa: " + e.getMessage();
        }
    }

    // Obter todas as pessoas. (LIST)
    private static String listarPessoas() {
        try {
            StringBuilder response = new StringBuilder();

            if (pessoas.isEmpty()) {
                return "Nenhuma pessoa cadastrada.";
            }
            
            response.append(String.format("%02d", pessoas.size())).append("\n");
            for (Pessoa pessoa : pessoas.values()) {
                response.append(pessoa.toString()).append("\n");
            }

            response.append("END_OF_LIST");
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao listar pessoas: " + e.getMessage();
        }
    }

    // Inserir nova equipe. (INSERT_EQUIPE;nome da equipe;cpf adm;cpf membro)
    private static String inserirEquipe(String[] parts) {
        try {
            if (parts.length != 4) {
                return "São necessários 4 dados para inserir equipe.";
            }
            
            String nome = parts[1];
            String cpfAdmin = parts[2];
            String cpfMembro = parts[3];

            Administrador administrador = (Administrador) pessoas.get(cpfAdmin);
            Membro membro = (Membro) pessoas.get(cpfMembro);

            if (administrador == null || membro == null) {
                return "Administrador ou Membro não encontrado.";
            }

            // Verificar se o Administrador já está associado a outra equipe
            for (Equipe equipe : equipes.values()) {
                if (equipe.getAdministrador().equals(administrador)) {
                    return "O Administrador já está associado à equipe: " + equipe.getId() + " - " + equipe.getNome();
                }
            }

            // Verificar se o Membro já está associado a outra equipe
            for (Equipe equipe : equipes.values()) {
                if (equipe.getMembros().contains(membro)) {
                    return "O Membro já está associado à equipe: " + equipe.getId() + " - " + equipe.getNome();
                }
            }

            // Atualizando os atributos de data e hora de entrada do Membro
            membro.setDataEntrada(LocalDate.now());
            membro.setHoraEntrada(LocalTime.now());

            Equipe equipe = new Equipe(nome, administrador, membro);
            equipes.put(equipe.getId(), equipe);
            return "Equipe inserida com sucesso.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao inserir equipe.";
        }
    }

    // Obter adm e membros associados pelo id da equipe. (GET_EQUIPE;id equipe)
    private static String obterEquipe(String id) {
        try {
            Long equipeId = Long.parseLong(id);
            Equipe equipe = equipes.get(equipeId);
            if (equipe != null) {
                StringBuilder response = new StringBuilder(equipe.toString());
                response.append("\nEND_OF_EQUIPE");
                return response.toString();
            } else {
                return "Equipe não encontrada";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao obter equipe: " + e.getMessage();
        }
    }

    // Obter IDs e nomes de todas as equipes. (LIST_EQUIPES)
    private static String obterEquipes() {
        try {
            StringBuilder response = new StringBuilder();
            if (equipes.isEmpty()) {
                return "Nenhuma equipe cadastrada.";
            }

            for (Equipe equipe : equipes.values()) {
                response.append("ID: ").append(equipe.getId()).append(" - Nome: ").append(equipe.getNome()).append("\n");
            }

            response.append("END_OF_LIST_EQUIPES");
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao obter equipes: " + e.getMessage();
        }
    }

    // Adiciona um membro a uma equipe existente. (INSERT_MEMBRO_EQUIPE;id equipe;cpf membro)
    private static String inserirMembroEquipe(String[] parts) {
        try {
            if (parts.length != 3) {
                return "São necessários 3 dados para inserir membro à equipe.";
            }
            
            Long equipeId = Long.parseLong(parts[1]);
            String cpfMembro = parts[2];

            Equipe equipe = equipes.get(equipeId);
            Membro membro = (Membro) pessoas.get(cpfMembro);

            if (equipe != null && membro != null) {
                // Verificar se o Membro já está associado a outra equipe
                for (Equipe e : equipes.values()) {
                    if (e.getMembros().contains(membro)) {
                        return "Membro já está associado à equipe: " + e.getId() + " - " + e.getNome();
                    }
                }
                
                // Atualizando os atributos de data e hora de entrada do Membro
                membro.setDataEntrada(LocalDate.now());
                membro.setHoraEntrada(LocalTime.now());

                equipe.adicionarMembro(membro);
                return "Membro " + membro.getNome() + " adicionado(a) à equipe " + equipeId + " com sucesso.";
            } else {
                return "Equipe ou Membro não encontrado.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao adicionar membro à equipe: " + e.getMessage();
        }
    }
    
    // Remove uma equipe e adm/membros associados. (DELETE_EQUIPE;id equipe)
    private static String removerEquipe(String[] parts) {
        try {
            if (parts.length != 2) {
                return "São necessários 2 dados para remover equipe.";
            }
            
            Long equipeId = Long.parseLong(parts[1]);
            Equipe equipe = equipes.get(equipeId);

            if (equipe == null) {
                return "Equipe não encontrada.";
            }

            // Obter o administrador e membros da equipe
            Administrador admin = equipe.getAdministrador();
            Membro[] membros = equipe.getMembros().toArray(new Membro[0]);

            // Remover a equipe, adm e membros
            equipes.remove(equipeId);
            pessoas.remove(admin.getCpf());

            for (Membro membro : membros) {
                pessoas.remove(membro.getCpf());
            }

            return "Equipe " + equipe.getNome() + " e seus componentes foram removidos com sucesso.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao remover equipe: " + e.getMessage();
        }
    }

}
