# Ejercicios de concurrencia propuestos

Estos cuatro ejercicios son propuestas de práctica basadas en las ideas comentadas en la conversación. No son enunciados confirmados del examen ni ejercicios atribuidos literalmente al profesor.

## 1. Carrera de eliminatorias

Se quiere simular una competición de atletismo con ocho corredores.

- Cada corredor debe ejecutarse en un hilo independiente.
- Todos los corredores deben esperar hasta que estén preparados los demás.
- Un juez dará la salida simultáneamente.
- Cada corredor tardará un tiempo aleatorio en completar la carrera.
- Se debe registrar el orden de llegada.
- El último corredor queda eliminado.
- Se repite la carrera con los participantes restantes hasta que quede un único ganador.
- Al final se mostrará el ganador.

**Conceptos para practicar:** `Thread`, `Runnable`, `CountDownLatch`, `AtomicInteger`, `Thread.sleep()` y `join()`.

## 2. Juego del pañuelo

Se quiere simular el juego del pañuelo entre dos equipos de cinco jugadores.

- Cada jugador se representa mediante un hilo cuando participa en una ronda.
- En cada ronda sale un jugador de cada equipo.
- Los dos jugadores esperan a que el árbitro dé la salida.
- Después corren durante un tiempo aleatorio hacia el pañuelo.
- Solo un jugador puede conseguir el pañuelo.
- El equipo del jugador que lo consigue suma un punto.
- Se realizan cinco rondas y se muestra el equipo ganador.

**Conceptos para practicar:** `Thread`, `Semaphore`, `CountDownLatch`, `AtomicInteger` y `join()`.

## 3. Concurso de preguntas

Se quiere simular un concurso con ocho participantes y un máximo de doce preguntas.

- Cada concursante se representa mediante un hilo.
- En cada pregunta, todos esperan a que el presentador dé la señal.
- Los concursantes tardan un tiempo aleatorio en pulsar el botón.
- Solo el primer concursante que pulse puede responder.
- Si acierta, suma un punto y continúa.
- Si falla, queda eliminado.
- El concurso termina cuando queda un solo participante o cuando se han realizado todas las preguntas.
- Si se alcanza el límite de preguntas, gana el participante que tenga más puntos.

**Conceptos para practicar:** `Thread`, `AtomicBoolean`, `compareAndSet()`, `CountDownLatch`, `ArrayList` y `join()`.

## 4. Carrera de relevos

Se quiere simular una carrera de relevos con cuatro equipos y tres corredores por equipo.

- Cada corredor se representa mediante un hilo.
- Todos los equipos esperan a que el juez dé la salida.
- El primer corredor de cada equipo comienza la carrera.
- Cuando termina su tramo, entrega el testigo al segundo corredor.
- El segundo entrega el testigo al tercero.
- Un corredor no puede empezar hasta recibir el testigo.
- El último corredor registra la llegada de su equipo.
- Se muestra la clasificación final.

**Conceptos para practicar:** `Thread`, `BlockingQueue`, `ArrayBlockingQueue`, `CountDownLatch`, `put()`, `take()` y `join()`.
