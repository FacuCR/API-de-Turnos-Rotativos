package com.neoris.api.exception;

import java.text.MessageFormat;

public class VacacionesException extends JornadaException{
    private static final String messageTemplate = "Ya hay unas vaciones asignadas en el año ({0}), intenta actualizarlas!";

    public VacacionesException(int anio) {
        super(MessageFormat.format(messageTemplate, anio));
    }
}
