package br.com.ertic.util.geo;

import br.com.ertic.util.infraestructure.log.Log;

public class TimeCount {

    private String name;
    private long inicio;

    private TimeCount(String name) {
        this.name = name;
        inicio = System.currentTimeMillis();
    }

    public static TimeCount start(String name) {
        return new TimeCount(name);
    }

    public void end() {
        Long tempo = System.currentTimeMillis() - inicio;
        Log.debug(this.getClass(), name + " - " + tempo);
    }
}
