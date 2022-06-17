package com.neoris.api.exception;

public class FechaDeVacacionesException extends  JornadaException{
    private static final String messageTemplate = "El turno se esta intentando guardar en una fecha de vacaciones del empleado!";

    public FechaDeVacacionesException() {
        super(messageTemplate);
    }
}
