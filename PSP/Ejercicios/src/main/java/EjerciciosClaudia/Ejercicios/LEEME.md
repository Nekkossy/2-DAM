# Paquete Ejercicios – Concurrencia Java (2.º DAM)

## Qué contiene

Este paquete reúne 12 ejercicios con enunciado resumido y solución comentada. Está preparado para copiarse dentro de `src/main/java` de tu proyecto IntelliJ.

### Orden recomendado de estudio

1. `ContadorVisitas.java` – condiciones de carrera, `synchronized` y `AtomicInteger`.
2. `CuentaRegresiva.java` – `sleep()` y `join()`.
3. `CocineroCancelable.java` – `interrupt()` y estados de hilo.
4. `ProductorConsumidor.java` – `BlockingQueue`, `put()` y `take()`.
5. `SalidaCarrera.java` – `CountDownLatch`.
6. `TrabajoEnFases.java` – `CyclicBarrier`.
7. `RecursoTryLock.java` – `ReentrantLock` y `tryLock()`.
8. `BancoVirtual.java` – sección crítica, `ReentrantLock` y `synchronized`.
9. `RestauranteConcurrente.java` – varios productores y consumidores, cierre ordenado.
10. `JuegoSillas.java` – simulación basada en la pista que te han contado.
11. `ExamenParking.java` – ejercicio modificado de parking con caravanas y barrera prioritaria.
12. `SistemaDescargas.java` – pool limitado, prioridades y tareas asíncronas.

## Cómo añadirlo a IntelliJ

1. Descomprime el ZIP.
2. Copia la carpeta `Ejercicios` dentro de `src/main/java` de tu proyecto.
3. Comprueba que cada archivo empieza por `package Ejercicios;`.
4. Abre la clase que quieras practicar y ejecuta su método `main()`.

La estructura resultante será:

```text
TuProyecto/
  src/
    main/
      java/
        Ejercicios/
          ContadorVisitas.java
          BancoVirtual.java
          RestauranteConcurrente.java
          ExamenParking.java
          SistemaDescargas.java
          JuegoSillas.java
          SalidaCarrera.java
          TrabajoEnFases.java
          ProductorConsumidor.java
          CocineroCancelable.java
          CuentaRegresiva.java
          RecursoTryLock.java
```

## Requisitos

- JDK 21 o superior.
- El paquete usa hilos virtuales (`Thread.ofVirtual()`), disponibles desde Java 21.
- Cada clase es independiente y tiene su propio `main()`. Ejecuta una clase cada vez.
- No hay que ejecutar todas a la vez.

## Importante sobre la procedencia

- Los problemas de contador, banco, restaurante, parking y descargas están basados en los enunciados del ZIP del profesor.
- Carrera, trabajo en fases, productor-consumidor, cocinero cancelable, cuenta regresiva y tryLock aparecen también en los ejercicios Java 25 del profesor.
- `ExamenParking.java` adapta el parking a las modificaciones concretas que has compartido: cierre controlado, ingresos separados, espera temporizada, barrera única con prioridad de salidas y caravanas.
- `JuegoSillas.java` es una propuesta de práctica basada en el rumor del examen. No aparece como ejercicio en los archivos del profesor y no podemos asegurar que el examen del año pasado fuera exactamente así.

## Nota de ejecución

Algunos ejercicios simulan esperas con `Thread.sleep()`. En `RestauranteConcurrente` y `SistemaDescargas` los tiempos se han reducido para que puedas probarlos más rápido. En `ExamenParking` puedes reducir las constantes de caravanas, llegadas, barrera y estancia para hacer una prueba rápida.
