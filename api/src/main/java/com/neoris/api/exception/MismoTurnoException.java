package com.neoris.api.exception;

import com.neoris.api.enums.ETurno;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MismoTurnoException extends JornadaException{
    private static final SimpleDateFormat df = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));
    private static final String messageTemplate = "No se pudo guardar los datos del turno por que ya hay un {0}  el {1}!";

    public MismoTurnoException(ETurno turno, Date fecha) {
        super(MessageFormat.format(messageTemplate, turno, df.format(fecha)));
    }
}
