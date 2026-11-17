# Enunciados de práctica de concurrencia – 2.º DAM

Este documento acompaña a las clases Java del paquete `Ejercicios`.

## Cómo interpretar el nivel de certeza

- **Material del profesor:** el ejercicio aparece en los archivos `problemas.md` o `ejercicios-hilos-java25.md` del ZIP que has compartido. Aquí se ha resumido su enunciado para tenerlo junto al código.
- **Adaptación de un ejercicio del profesor:** conserva su idea, pero se han incorporado las modificaciones de tu actividad concreta.
- **Propuesta por la pista del examen:** es una reconstrucción para practicar a partir de lo que te han contado; no tenemos el enunciado original.

---

## 1. Contador de visitas web – Material del profesor

Crea una aplicación que simule un contador de visitas. Debe crear 1000 hilos que representen usuarios visitando una página. Cada hilo incrementa el contador una vez y espera un tiempo aleatorio antes de hacerlo.

Implementa y compara tres versiones:

1. Sin sincronización, para observar una posible condición de carrera.
2. Con `synchronized`.
3. Con `AtomicInteger`.

Al terminar, espera a todos los hilos y muestra el valor final y el tiempo empleado.

**Fíjate en:** `join()`, `synchronized`, `AtomicInteger`, `incrementAndGet()`.

## 2. Banco virtual – Material del profesor

Simula una cuenta bancaria con saldo inicial de 10.000 euros. Crea 50 clientes concurrentes y haz que cada uno realice 10 operaciones aleatorias. El 60 % serán retiradas de 1 a 100 euros y el 40 % ingresos de 1 a 50 euros.

El saldo no puede quedar negativo. Si no hay dinero suficiente, la retirada se rechaza. Registra las operaciones y muestra el saldo final.

La solución de práctica compara dos implementaciones correctas: `ReentrantLock` y `synchronized`.

**Fíjate en:** sección crítica, lock/unlock, `finally`, métodos `synchronized` y `join()`.

## 3. Restaurante concurrente – Material del profesor, simplificado para practicar

Simula un restaurante con 100 clientes, 5 camareros y 3 cocineros. Los clientes generan pedidos. Los camareros trasladan los pedidos a una cola de cocina de capacidad 10. Los cocineros recogen pedidos y preparan los platos.

La cola debe coordinar automáticamente a los trabajadores: si está llena, el camarero espera; si está vacía, el cocinero espera. Cada cliente termina cuando su pedido está preparado.

Al cerrar, detén primero a los camareros y después a los cocineros mediante pedidos especiales de cierre. No fuerces el cierre del programa.

**Fíjate en:** productor-consumidor, `BlockingQueue`, `put()`, `take()`, `CountDownLatch` y cierre ordenado.

## 4. Parking inteligente con caravanas – Adaptación del ejercicio del profesor

Gestiona un parking con 20 plazas normales y 5 VIP. La actividad modificada pide caravanas de entre 1 y 3 coches NORMALES. Todas las plazas de una caravana deben reservarse juntas y contiguas; si no consigue todas antes del tiempo límite, se marcha.

Cada coche necesita pasar por la barrera de entrada y por la de salida. En lugar de dos barreras independientes, un único hilo atiende solicitudes. Antes de atender una entrada, debe comprobar siempre si hay salidas pendientes y darles prioridad.

Simula las estancias, libera todas las plazas al salir, muestra un dashboard periódico, registra ingresos normales y VIP por separado y realiza un cierre ordenado. Como esta versión utiliza caravanas normales, los ingresos VIP serán cero.

**Fíjate en:** `Semaphore.tryAcquire(cantidad, tiempo, unidad)`, `BlockingQueue`, `CountDownLatch`, hilo de barrera, prioridad de salidas, `ScheduledExecutorService`, `shutdown()`, `awaitTermination()` y `join()`.

## 5. Sistema de descargas – Material del profesor, versión de práctica reducida

Simula 50 usuarios que solicitan archivos. El servidor procesa como máximo 5 descargas al mismo tiempo. Los usuarios premium deben tener prioridad sobre los normales. Una descarga puede fallar y reintentarse hasta un máximo de 3 intentos.

Muestra el progreso de las descargas y las estadísticas finales. Los tiempos del código se han reducido para que la ejecución no dure varios minutos.

**Fíjate en:** `ThreadPoolExecutor`, `PriorityBlockingQueue`, `CompletableFuture`, `ConcurrentHashMap`, `ScheduledExecutorService` y cierre de ejecutores.

## 6. Juego de las sillas – Propuesta basada en la pista del examen

Este ejercicio **no aparece en los apuntes compartidos**. Se ha reconstruido a partir de la información que te han dado.

Empiezan 10 jugadores y hay una silla menos que jugadores. Cada jugador es un hilo. Mientras la música está sonando, los jugadores bailan. Cuando la música se detiene, todos intentan sentarse. La cola representa las sillas disponibles y tiene capacidad limitada. Si `offer()` devuelve `false`, el jugador queda eliminado.

Después se elimina una silla y se repite la ronda hasta que queda un ganador.

**Fíjate en:** `Runnable`, hilos, `AtomicBoolean`, `BlockingQueue`, `offer()`, `CountDownLatch`, `sleep()` y `join()`.

## 7. Salida de carrera – Material del profesor

Cinco corredores calientan durante un tiempo aleatorio. Ninguno puede empezar a correr hasta que todos estén preparados. El juez espera a que todos lleguen y después da la señal de salida. El programa espera al final a que terminen todos los hilos.

**Fíjate en:** dos `CountDownLatch`: uno para contar corredores preparados y otro para bloquear la salida hasta que el juez la dé.

## 8. Trabajo en fases – Material del profesor

Cuatro trabajadores realizan tres fases de un proyecto. Ningún trabajador puede iniciar la siguiente fase hasta que todos hayan terminado la fase actual.

**Fíjate en:** `CyclicBarrier(4)`, `await()` y reutilización de la barrera en varias fases.

## 9. Productor-consumidor con cola limitada – Material del profesor

Un productor genera 20 números y un consumidor los recoge. La cola tiene capacidad 5. El productor debe esperar cuando la cola está llena y el consumidor debe esperar cuando está vacía.

**Fíjate en:** `ArrayBlockingQueue`, `put()`, `take()` y `join()`.

## 10. Cocinero cancelable – Material del profesor

Un hilo cocina un plato durante 10 segundos. El hilo principal espera 3 segundos y solicita cancelar la cocción mediante `interrupt()`. El cocinero captura la interrupción, informa de que se ha cancelado y termina limpiamente.

**Fíjate en:** `interrupt()`, `InterruptedException`, `isInterrupted()`, `getState()` y `join()`.

## 11. Cuenta regresiva – Material del profesor

Un hilo cuenta de 10 a 0, esperando un segundo entre números. El hilo principal tiene que esperar a que ese hilo termine con `join()` antes de imprimir «¡Despegue!».

**Fíjate en:** `Thread`, `sleep()` y `join()`.

## 12. Recurso compartido con tryLock – Material del profesor

Dos hilos quieren utilizar un recurso protegido por `ReentrantLock`. El segundo solo espera como máximo 500 milisegundos. Si no consigue el lock, muestra un mensaje y no se queda bloqueado indefinidamente.

**Fíjate en:** `ReentrantLock`, `tryLock(500, TimeUnit.MILLISECONDS)`, `unlock()` y `finally`.

---

## Posibles ampliaciones para practicar

No son enunciados confirmados del examen, sino cambios que podrían añadirse a cualquiera de los ejercicios:

- Añadir un contador de tareas terminadas con `AtomicInteger`.
- Hacer que los hilos tengan nombres identificables.
- Añadir una prioridad VIP o urgente a una cola.
- Limitar el número de recursos simultáneos mediante `Semaphore`.
- Dar un tiempo máximo para conseguir un recurso con `tryAcquire()`.
- Cancelar una tarea con `interrupt()`.
- Esperar a todos los hilos con `join()` o `CountDownLatch`.
- Cerrar un `ExecutorService` con `shutdown()` y `awaitTermination()`.
- Mostrar estadísticas periódicamente con `ScheduledExecutorService`.
