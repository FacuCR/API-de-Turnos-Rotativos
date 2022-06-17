package com.neoris.api.exception;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FechaAnteriorException extends JornadaException{
    private static final String messageTemplate = "La fecha ({0}) es anterior a la fecha de hoy {1}, ingrese una fecha valida!";
    private static final Date fechaDeHoy = new Date();
    private static final SimpleDateFormat df = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

    public FechaAnteriorException(Date fecha) {
        super(MessageFormat.format(messageTemplate, df.format(fecha), df.format(fechaDeHoy)));
    }
}
