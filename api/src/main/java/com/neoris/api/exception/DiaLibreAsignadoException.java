package com.neoris.api.exception;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaLibreAsignadoException extends JornadaException{
    private static final String messageTemplate = "No se pudo guardar el dia libre por que ya tienes un dia libre asignado el {0}!";
    private static final SimpleDateFormat df = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

    public DiaLibreAsignadoException(Date fecha) {
        super(MessageFormat.format(messageTemplate, df.format(fecha)));
    }
}
