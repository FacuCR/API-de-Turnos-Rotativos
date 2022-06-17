package com.neoris.api.exception;

import com.neoris.api.enums.ETurno;

import java.text.MessageFormat;

public class MaxTurnosDiariosException extends JornadaException{
    private static final String messageTemplate = "No se pudo guardar los datos del turno por que los {0} de ese dia estan ocupados!";

    public MaxTurnosDiariosException(ETurno turno) {
        super(MessageFormat.format(messageTemplate, turno));
    }
}
