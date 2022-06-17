package com.neoris.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@NoArgsConstructor
@Entity
public class Vacaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long idVacaciones;
    @Getter private Date fechaInicio;
    @Getter private Date fechaFinal;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jornada_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    @Getter @Setter private JornadaLaboral jornadaId;

    public void setFechaInicio(Date fechaInicio, int antiguedad) {
        this.fechaInicio = fechaInicio;
        this.fechaFinal = agregarDias(fechaInicio, diasDeVacacionesCorrespondientes(antiguedad));
    }

    // Metodo que le agrega dias a una fecha
    public static Date agregarDias(Date fecha, int dias)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.DATE, dias); // numeros negativos disminuyen los dias
        return cal.getTime();
    }

    public static int diasDeVacacionesCorrespondientes(int antiguedad) {
        if (antiguedad < 1) {
            return 14;
        } else if(antiguedad < 5) {
            return 21;
        } else if (antiguedad < 10) {
            return 28;
        } else {
            return 35;
        }
    }
}
