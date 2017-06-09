package br.com.ertic.util.geo;

import br.com.ertic.util.infraestructure.log.Log;

public class TimeCount {

    private Class<?> clazz;
    private String name;
    private long inicio;

    private TimeCount(Class<?> clazz, String name) {
        this.name = name;
        this.clazz = clazz;
        inicio = System.currentTimeMillis();
    }

    public static TimeCount start(Class<?> clazz, String name) {
        return new TimeCount(clazz, name);
    }

    public long end() {
        Long tempo = System.currentTimeMillis() - inicio;
        Log.debug(clazz, name + " - " + tempo);
        return tempo;
    }
}
