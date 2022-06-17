package com.neoris.api.exception;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class YaHayUnTurnoException extends JornadaException{
    private static final String messageTemplate = "No se pudo guardar el turno {1} por que ya tienes un turno {1} asignado el {0}!";
    private static final SimpleDateFormat df = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

    public YaHayUnTurnoException(Date fecha, String tipoDeTurno) {
        super(MessageFormat.format(messageTemplate, df.format(fecha), tipoDeTurno));
    }
}
