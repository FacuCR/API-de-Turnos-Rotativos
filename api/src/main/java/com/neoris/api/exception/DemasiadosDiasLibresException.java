package com.neoris.api.exception;

public class DemasiadosDiasLibresException extends JornadaException{
    private static final String messageTemplate = "No se pudo guardar el dia libre por que ya tienes dos dias libres asignados en ese semana, intenta actualizarlos!";

    public DemasiadosDiasLibresException() {
        super(messageTemplate);
    }
}
