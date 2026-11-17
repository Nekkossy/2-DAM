
package apuntes.escaperoom.modelo;

public class Partida {

    private String[] preguntas;
    private String[] respuestas;
    private int vidas;
    private int pruebaActual;
    private int pruebasSuperadas;
    private boolean terminada;
    private boolean ganada;

    public Partida() {
        this.preguntas = new String[]{
                "¿Cuanto es 9 x 9?",
                "¿Que numero sigue? 2, 4, 8, 16...",
                "Tengo 3 manzanas y cojo 2. ¿Cuantas tengo?"
        };

        this.respuestas = new String[]{"56", "32", "2"};

        this.vidas = 3;
        this.pruebaActual = 0;
        this.pruebasSuperadas = 0;
        this.terminada = false;
        this.ganada = false;
    }

    public String getPreguntaActual() {
        return preguntas[pruebaActual];
    }

    public String getRespuestaActual() {
        return respuestas[pruebaActual];
    }

    public int getVidas() {
        return vidas;
    }

    public void setVidas(int vidas) {
        this.vidas = vidas;
    }

    public int getPruebaActual() {
        return pruebaActual;
    }

    public void setPruebaActual(int pruebaActual) {
        this.pruebaActual = pruebaActual;
    }

    public int getPruebasSuperadas() {
        return pruebasSuperadas;
    }

    public void setPruebasSuperadas(int pruebasSuperadas) {
        this.pruebasSuperadas = pruebasSuperadas;
    }

    public boolean isTerminada() {
        return terminada;
    }

    public void setTerminada(boolean terminada) {
        this.terminada = terminada;
    }

    public boolean isGanada() {
        return ganada;
    }

    public void setGanada(boolean ganada) {
        this.ganada = ganada;
    }
}
