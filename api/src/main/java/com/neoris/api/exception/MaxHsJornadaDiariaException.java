package com.neoris.api.exception;

public class MaxHsJornadaDiariaException extends JornadaException{
    private static final String messageTemplate = "No se pudo guardar los datos del turno por que supera el limite de horas diarias(12hs)!";

    public MaxHsJornadaDiariaException() {
        super(messageTemplate);
    }
}
