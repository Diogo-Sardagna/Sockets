package trabalho1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Notebook
 */
public class Equipe implements Serializable {
    private static Long count = 1L;
    private Long id;
    private String nome;
    private List<Membro> membros;
    private Administrador administrador;

    public Equipe(String nome, Administrador administrador, Membro membroInicial) {
        if (administrador == null || membroInicial == null) {
            throw new IllegalArgumentException("A equipe deve ter pelo menos um Administrador e um Membro.");
        }
        
        this.id = count++;
        this.nome = nome;
        this.administrador = administrador;
        this.membros = new ArrayList<>();
        this.membros.add(membroInicial);
    }

    // Adicionar um membro à equipe
    public void adicionarMembro(Membro membro) {
        this.membros.add(membro);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Membro> getMembros() {
        return membros;
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Equipe ID: ").append(this.id).append("\n");
        sb.append("Nome: ").append(this.nome).append("\n");
        sb.append("Administrador: ").append(administrador.toString()).append("\n");
        sb.append("Membros:\n");

        for (Membro membro : membros) {
            sb.append(membro.toString()).append("\n");
        }

        return sb.toString().trim();
    }

}
