package trabalho1;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Notebook
 */
public class Membro extends Pessoa {
    private LocalDate dataEntrada;
    private LocalTime horaEntrada;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Membro(String cpf, String nome, String endereco, LocalDate dataEntrada, LocalTime horaEntrada) {
        super(cpf, nome, endereco);
        this.dataEntrada = dataEntrada;
        this.horaEntrada = horaEntrada;
    }

    // Getters e Setters
    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    @Override
    public String toString() {
        return super.toString() + ";" +
               (dataEntrada != null ? dataEntrada.format(DATE_FORMATTER) : "N/A") + ";" +
               (horaEntrada != null ? horaEntrada.format(TIME_FORMATTER) : "N/A");
    }
}
