package com.neoris.api.exception;

public class MaxHsJornadaSemanalException extends JornadaException{
    private static final String messageTemplate = "No se pudo guardar los datos del turno por que supera el limite de horas(48hs semanales)!";

    public MaxHsJornadaSemanalException() {
        super(messageTemplate);
    }
}
