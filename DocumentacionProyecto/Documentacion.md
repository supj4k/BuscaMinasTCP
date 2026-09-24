## informe técnico de funcionamiento
Aclaración importante del estado del proyecto del LAB, para compilar el proyecto con los dos Subproyectos multimodos tenemos que ubicarnos en la raíz del proyecto que en este caso es BuscaMinasProfeLAB,  y digitar el siguiente comando:   '.\gradlew build'  

muestra de que pasa la compilación:


![Compilacion BUILD SUCCESSFUL](img/01-build-successful.png)
![Detalle de la compilacion exitosa](img/02-build-successful-detalle.png)

## Funcionamiento Código
primero vamos a poner a correr el main del server en la terminal para así luego darle a correr el mainClient desde el compilador de intellij, el comando para correr el server desde la terminal será el siguiente:  ".\gradlew :server:run" 

![Servidor corriendo con gradlew :server:run](img/03-server-corriendo.png)

entramos primero que ante todo a la raíz del proyecto aclaración importante para que funcione correctamente; además ya tengo dos terminal una para server y otra para el segundo cliente

![Dos terminales: servidor y segundo cliente](img/04-dos-terminales.png)

![Servidor escuchando en el puerto 12345](img/05-server-escuchando-12345.png)


ahí el server esta corriendo completamente bien y esta escuchando o esperando request en el puerto 12345; ahora correremos el mainclient desde intellij para despues hacerlo con un segundo cliente en la segunda terminal 

![Cliente ejecutado desde IntelliJ](img/06-cliente-intellij.png)

![Menu del cliente](img/07-menu-cliente.png)



![Menu del cliente](img/08-menu-cliente-2.png)


ahora crearemos un tablero 6x6 y para ejecutar cliente de nuevo en otra terminal con el siguiente comando ".\gradlew :client:run"

![Segundo cliente con gradlew :client:run](img/09-segundo-cliente-run.png)

![Segundo cliente en ejecucion](img/10-segundo-cliente.png)

se creo y desde el otro cliente se puede ver el tablero 
![Tablero 6x6 visible desde el otro cliente](img/11-tablero-6x6-otro-cliente.png)

vamos a seleccionar destapar una celda y ver si aparece actualizado en el otro cliente es decir el segundo
![Destapar una celda](img/12-destapar-celda.png)



si se actualiza:
![Tablero actualizado tras destapar](img/13-tablero-actualizado.png)



![Destapar otra celda](img/14-destapar-otra-celda.png)
a ver si aparece en el otro cliente: 
![Actualizacion reflejada en el otro cliente](img/15-actualizado-otro-cliente.png)
en efecto si se le actualizo; ahora vamos a marcar una casilla oculta y ver si aparece en el otro tablero del segundo cliente:
![Marcar bandera en casilla oculta](img/16-marcar-bandera.png)

terminal segundo cliente:

![Bandera reflejada en el segundo cliente](img/17-bandera-otro-cliente.png)
si le actualizo el tablero con la posición de la bandera o donde se marco; voy a intentar darle a una mina y ver si al otro cliente al pedir estado del tablero le muestra las minas, y sino pues le daremos a rendirnos
![Estado antes de pisar la mina](img/18-antes-de-la-mina.png)
pisamos la mina miremos si se le actualiza:

![Mina pisada, tablero actualizado](img/19-mina-pisada-actualizada.png)
si se le actualiza y esta bien todo el proyecto de buscaminas TCP con multimodos y multihilo/multiconexion; gracias a la thread pool que tenemos en TCPcontroller; ya para terminar verificaremos que funciona lo de rendirse y que muestre todo el tablero donde estaban las minas, y que se le actualice al otro cliente

![Rendirse: tablero completo revelado](img/20-rendirse-tablero-completo.png)


le daremos a la opcion 5 desde el cliente 2 de la terminal

![Opcion 5 (rendirse) desde el cliente 2](img/21-opcion-rendirse-cliente2.png)

se le actualiza el tablero al otro cliente

![Tablero revelado actualizado en el otro cliente](img/22-tablero-revelado-otro-cliente.png)

podemos decir que el proyecto funciona perfectamente

# Cuestionario Conceptual LAB


### 1. Delimitación de Mensajes en TCP: TCP es un protocolo de transporte orientado a flujo de bytes continuo y no a bloques. ¿Por que se produciría un fallo de lectura o bloqueo indefinido si el cliente enviara el JSON sin el salto de lineal (\n) o sin invocar "writer.flush()"? Explique el concepto de TCP Framing.

primero antes de responder la pregunta hay que saber que es TCP y como se comporta; El **TCP** (Transmission Control Protocol o **Protocolo de Control de Transmisión**) es un estándar de comunicación fundamental que permite a los dispositivos y aplicaciones intercambiar datos de forma segura y ordenada a través de una red como Internet. Su funcionamiento se basa orientado a la conexión, es decir se crea un enlace directo o una socket entre el emisor (en nuestro caso el servidor) y el receptor (en nuestro caso el cliente), para hacer un Handshake o saludo, donde se declara el orden de bits y demás parámetros; por eso mismo divide sus paquetes o datos para que sea una entrega fiable y segura, donde comprueba que no haya sufrido daños y luego confirma la llegada de esa parte de los datos y los siguientes que espera. El receptor o el que recibe el mensaje tiene que organizar el flujo de datos para estructurar el mensaje y leerlo, y por eso en tal caso que se pierda algún paquete pide reenvió del mensaje

ya entendimos TCP, ahora hay que entender un concepto clave para resolver la pregunta el cual es el Framing, este concepto se aloja en la capa 2 de enlace de datos del modelo OSI o TCP/IP, y sirve para que el receptor sepa donde empieza y acaba la información útil del mensaje, para así empezar a organizar los datos para entender el mensaje, PDU en esta capa es la trama entonces añade cabeceras o marcas especiales de inicio y fin, así evitando que se mezclen o se pierdan los datos, para entender mejor la esencia del Framing voy a mostrar o explicar la estructura de una trama en la capa de enlace 
- **Cabecera (Header):** Contiene información de control. Incluye las direcciones físicas de origen y destino (como las direcciones MAC) y los indicadores de inicio.
- **Datos (Payload):** Es el paquete de datos real enviado desde las capas superiores (por ejemplo, un paquete IP con parte de tu mensaje).
- **Cola (Trailer):** Contiene bits que marcan el final de la trama y mecanismos de detección de errores.

Y para que el receptor identifique los límites de la trama se usan técnicas como:

- **Conteo de caracteres:** la cabecera indica cuántos bytes tiene la trama. Si ese número se corrompe, el receptor pierde la sincronización.
- **Byte stuffing:** se usan bytes especiales (banderas) para marcar inicio y fin; si ese byte aparece dentro de los datos, se le antepone un byte de escape.
- **Bit stuffing:** lo mismo pero a nivel de bits; por ejemplo, si `01111110` marca el límite, y en los datos aparecen cinco unos seguidos, se inserta un cero para no confundir al receptor.

Sin embargo, **ese framing de capa 2 es transparente para nuestra aplicación**. El punto crítico es este: aunque TCP garantiza que los bytes lleguen ordenados y sin errores, cuando los entrega a nuestro programa lo hace como un **flujo continuo de bytes, sin marcar dónde termina un mensaje y empieza otro**. Por eso, en la **capa de aplicación**, es responsabilidad del desarrollador definir su propia delimitación de mensajes. Ese es el framing que nos concierne en este laboratorio.

En este proyecto, esa delimitación es el salto de línea (`\n`) al final de cada trama JSON. El emisor escribe el JSON y agrega el `\n` con `writer.newLine()`, y el receptor lee exactamente un mensaje con `readLine()`, que consume bytes hasta encontrar ese salto de línea. Cabe aclarar que ambos extremos actúan como emisor y receptor según el momento: en una petición, el cliente emite y el servidor recibe; en la respuesta, se invierte.

De aquí se entiende la importancia del framing: sin él, el receptor pensará que la transmisión de datos aún no ha terminado y esperará indefinidamente. Es decir, si el JSON se envía **sin el salto de línea (`\n`)**, se produce un bloqueo infinito, porque `readLine()` seguirá esperando la continuación del mensaje —esperando a que "termine"— pero eso nunca ocurrirá, y el servidor quedará bloqueado. Lo mismo sucede si no se invoca `writer.flush()`: los datos se quedan acumulados en el buffer en memoria y nunca salen efectivamente por el socket, dejando de nuevo al receptor esperando.

El siguiente diagrama muestra el **flujo correcto** de una petición, con el `\n` y el `flush` en su lugar:


```mermaid
sequenceDiagram
    participant C as Cliente
    participant S as Servidor
    C->>C: gson.toJson(request)
    C->>S: JSON + "\n" (writer.newLine)
    C->>C: writer.flush() empuja el buffer
    Note over S: readLine() lee hasta ver el "\n"
    S->>S: procesa la peticion
    S->>C: JSON respuesta + "\n"
    C->>C: readLine() recibe la respuesta
```
el cliente serializa el objeto a JSON, lo envía terminado en `\n` y hace `flush()` para empujarlo por el socket. El servidor, gracias al `\n`, sabe dónde termina el mensaje, lo procesa y responde con otro JSON también delimitado por `\n`, que el cliente lee con `readLine()`. El framing (`\n`) es lo que permite que cada extremo sepa exactamente qué leer.


**Este otro diagrama muestra el caso defectuoso, cuando falta el `\n`:**

```mermaid
sequenceDiagram
    participant C as Cliente
    participant S as Servidor
    C->>S: JSON (SIN "\n")
    Note over S: readLine() sigue esperando<br/>un salto de linea que nunca llega
    Note over S: BLOQUEADO indefinidamente
    Note over C: espera una respuesta<br/>que nunca vendra
    Note over C,S: Ambos congelados (deadlock)
```
el cliente envía el JSON sin el delimitador. Como TCP entrega un flujo continuo sin fronteras, el `readLine()` del servidor no encuentra dónde termina el mensaje y queda bloqueado esperando indefinidamente. Al no responder el servidor, el cliente también queda esperando una respuesta que nunca llegará: ambos extremos terminan congelados en un _deadlock_.

### 2. Ventajas y Sobrecargas de ThreadPools: El servidor utiliza Executors.newFixedThreadPool(5). ¿Qué ventajas ofrece esta  estrategia frente al esquema ingenuo de instanciar un new Thread() por cada cliente? ¿Qué sucede cuando se conectan 6 ó mas clientes simultáneos?

Para responder esta pregunta primero hay que entender el problema que resuelve un ThreadPool. Un hilo (_thread_) es una unidad de ejecución que el servidor usa para atender a un cliente sin bloquear a los demás. El asunto es cómo se administran esos hilos, y ahí es donde el enfoque ingenuo y el ThreadPool se diferencian.

El enfoque ingenuo consiste en crear un `new Thread()` por cada cliente que se conecta. El problema es que crear un hilo tiene un costo: el sistema operativo debe reservarle memoria (su propia pila) y administrarlo. Entonces, si se conectan muchísimos clientes, el servidor tendría que crear un hilo por cada uno, y su rendimiento bajaría, porque habría una enorme cantidad de hilos consumiendo recursos. A esto se suma la sobrecarga de conmutación de contexto (_context switching_): cuando hay demasiados hilos, la CPU gasta mucho tiempo alternando entre ellos en lugar de trabajar. Y como no existe ningún límite, un pico de conexiones podría saturar la máquina.

Por eso es mejor usar un **ThreadPool** con una cantidad fija de hilos, como el `Executors.newFixedThreadPool(5)` de este proyecto. En lugar de crear y destruir un hilo por cliente, el pool crea 5 hilos una sola vez y los **reutiliza**: cuando un hilo termina de atender a un cliente, no se destruye, sino que queda disponible en el pool para atender al siguiente. Así se evita el costo constante de creación y destrucción, y se acota el consumo de recursos, porque nunca habrá más de 5 hilos activos al mismo tiempo, sin importar cuántos clientes lleguen.

Ahora bien, ¿qué sucede cuando se conectan **6 o más clientes en simultáneo**? Los primeros 5 son atendidos de inmediato, cada uno por uno de los 5 hilos del pool. El sexto cliente no provoca la creación de un nuevo hilo ni es rechazado: queda en una **cola de espera** interna del pool. Es decir, ese cliente queda por fuera esperando a que alguno de los 5 hilos se libere; en cuanto uno termina su tarea y vuelve al pool, toma al cliente que estaba en espera y lo atiende. El _trade-off_ de este enfoque es que la concurrencia real queda limitada a 5 operaciones simultáneas: si llegaran muchos clientes, el excedente tendría que esperar su turno.

El siguiente diagrama ilustra este comportamiento:

```mermaid
flowchart LR
    C1[Cliente 1] --> Q
    C2[Cliente 2] --> Q
    C3[Cliente 3] --> Q
    C4[Cliente 4] --> Q
    C5[Cliente 5] --> Q
    C6[Cliente 6] --> Q
    Q[Cola de espera] --> P

    subgraph P[ThreadPool fijo de 5 hilos]
        H1[hilo 1]
        H2[hilo 2]
        H3[hilo 3]
        H4[hilo 4]
        H5[hilo 5]
    end

    H1 --> BG[(BoardGame compartido)]
    H2 --> BG
    H3 --> BG
    H4 --> BG
    H5 --> BG
```


los clientes que se conectan pasan primero por la cola, desde donde el pool les asigna un hilo. Como el pool tiene 5 hilos, hasta 5 clientes son atendidos en paralelo, y todos operan sobre la misma instancia compartida de `BoardGame`. Cuando llega un sexto cliente (o más), permanece en la cola de espera hasta que uno de los hilos se desocupe y pueda atenderlo. El diagrama deja ver que el número de hilos es fijo y que la cola es el mecanismo que absorbe el exceso de clientes.

### 3. Condiciones de Carrera y Exclusión Mutua:  Si el servidor maneja una única instancia compartida de BoardGame, ¿Qué problema especifico de concurrencia ocurriría si dos clientes ejecutan selectCell en celdas contiguas exactamente al mismo milisegundo sin métodos synchronized?


Para responder esta pregunta primero hay que entender qué es una **condición de carrera** (_race condition_). Ocurre cuando dos o más hilos acceden y modifican un mismo recurso compartido al mismo tiempo, y el resultado final depende del orden impredecible en que se intercalen sus operaciones. En este servidor, ese recurso compartido es la **única instancia de `BoardGame`**, sobre la cual operan todos los hilos del ThreadPool. Como no hay un tablero por cliente sino uno solo para todos, cualquier acceso simultáneo es un punto de riesgo.

Si dos clientes en simultáneo intentan acceder al tablero compartido seleccionando una celda, puede pasar que, cuando ambos hilos le pidan al servidor actualizar el estado, uno de los dos cambios no se guarde correctamente, o que un hilo sobreescriba el trabajo del otro. Es decir, sin `synchronized` no se garantiza que los dos cambios se conserven: el resultado depende de quién "gane" la carrera.

El problema se agrava con la recursión de `showCells`. Cuando se destapa una celda con valor cero, esta función se expande recursivamente destapando las celdas vecinas (modificando su atributo `hide`). Si dos hilos ejecutan `selectCell` sobre celdas contiguas al mismo tiempo, ambas recursiones pueden estar recorriendo y modificando **celdas que se solapan**. Esto lleva a estados inconsistentes: un hilo puede leer una celda que el otro está modificando a medias, o uno puede sobreescribir un cambio que el otro acababa de hacer. Como consecuencia, el tablero que se retorna puede reflejar solo los cambios del último hilo que escribió, ignorando los del otro, o incluso puede quedar en un estado corrupto que ya no corresponde al real. En el peor caso, la recursión que recorre el arreglo puede toparse con un estado a medio actualizar y lanzar una excepción.

Esto lo soluciona **`synchronized`**, que funciona como un seguro (un _lock_ o cerrojo): cuando un hilo entra a un método sincronizado sobre el `BoardGame`, adquiere el seguro, y mientras lo tenga, ningún otro hilo puede entrar a ese recurso. Los demás hilos deben esperar a que lo libere para poder acceder. Así, aplicando `synchronized` a los métodos que mutan el estado del tablero (`selectCell`, `markCell`, `initGame`, `showAll`), se garantiza que cada operación se ejecute completa antes de que empiece la siguiente. Las operaciones dejan de intercalarse y el estado compartido nunca queda a medias.

El siguiente diagrama muestra cómo `synchronized` serializa el acceso de dos clientes:

```mermaid
sequenceDiagram
    participant C1 as Cliente 1
    participant H1 as hilo 1
    participant BG as BoardGame (synchronized)
    participant H2 as hilo 2
    participant C2 as Cliente 2

    C1->>H1: SELECT_CELL (2,3)
    C2->>H2: SELECT_CELL (2,4)
    H1->>BG: entra a selectCell (adquiere lock)
    Note over BG: hilo 2 debe ESPERAR<br/>el lock esta ocupado
    BG-->>H1: termina, libera lock
    H2->>BG: ahora si entra (adquiere lock)
    BG-->>H2: termina, libera lock
    Note over C1,C2: Sin choques: acceso serializado
```

los dos clientes envían casi al mismo tiempo una petición para destapar celdas contiguas. El hilo 1 llega primero y adquiere el _lock_ del `BoardGame`; mientras lo tiene, el hilo 2 no puede entrar y queda esperando. Solo cuando el hilo 1 termina y libera el seguro, el hilo 2 puede adquirirlo y ejecutar su operación. De este modo, aunque las peticiones llegaron simultáneamente, el acceso al tablero se realiza uno después del otro (serializado), y ambos cambios se conservan sin corromper el estado.
### 4. Conexiones Cortas vs. Conexiones Persistentes: En el diseño actual, el servidor cierra el socket tras responder cada comando (Short-lived connection). Compare este enfoque frente a mantener el socket abierto durante toda la partida (Persistent connection) en términos de consumo de descriptores de archivo, latencia y escalabilidad.
En el diseño actual, el servidor cierra el socket tras responder cada comando: es una **conexión corta** (_short-lived connection_), donde cada operación del juego (destapar, marcar, consultar) abre una conexión nueva, la usa una sola vez y la cierra. La alternativa es la **conexión persistente**, en la que el socket se abre una vez y permanece abierto durante toda la partida, transportando por él todos los comandos.

La comparación se puede analizar en tres aspectos:

**Consumo de descriptores de archivo.** La conexión corta es más eficiente en este punto: como el socket se cierra apenas termina cada comando, libera el recurso casi de inmediato, y en cualquier instante hay muy pocos sockets abiertos. Esto permite que muchos usuarios distintos usen el servicio sin agotar los descriptores del sistema operativo. La conexión persistente, en cambio, mantiene un socket abierto por cada jugador durante toda la partida —nunca lo libera hasta el final—, por lo que tiene un consumo mucho mayor y, con muchos jugadores simultáneos, corre el riesgo de agotar los descriptores.

**Latencia.** Aquí la relación se invierte. La conexión corta es más costosa, porque tiene que hacer un _handshake_ TCP (el saludo de establecimiento de conexión) cada vez que llega un comando; ese costo se paga una y otra vez a lo largo de la partida. La conexión persistente, al mantener el socket abierto, hace el handshake una sola vez al inicio y luego los comandos viajan directamente, reduciendo mucho la latencia por operación.

**Escalabilidad.** La conexión corta escala mejor en número de usuarios, porque al liberar recursos rápido puede rotar entre muchos clientes. La persistente escala peor en ese sentido, y en este proyecto hay un detalle importante: como el servidor usa un ThreadPool de 5 hilos y cada conexión ocupa un hilo mientras está activa, una conexión persistente **ataría un hilo del pool a un solo jugador durante toda su partida**. Eso significa que solo 5 jugadores podrían jugar al mismo tiempo, y el resto tendría que esperar en la cola sin poder jugar. Con conexión corta, en cambio, el hilo se libera después de cada comando, así que muchos más jugadores pueden compartir los 5 hilos.

En conclusión, la conexión corta sacrifica latencia (por el handshake repetido) a cambio de un uso eficiente de recursos y mejor escalabilidad en número de usuarios; la persistente sacrifica recursos (sockets y hilos ocupados de forma permanente) a cambio de mucha menor latencia por comando.

El siguiente diagrama muestra la **conexión corta**, el enfoque actual:
```mermaid
sequenceDiagram
    participant C as Cliente
    participant S as Servidor
    Note over C,S: Comando 1
    C->>S: handshake TCP (abrir)
    C->>S: SELECT_CELL
    S->>C: respuesta
    C->>S: cerrar socket
    Note over C,S: Comando 2
    C->>S: handshake TCP (abrir de nuevo)
    C->>S: MARK_CELL
    S->>C: respuesta
    C->>S: cerrar socket
    Note over C,S: Un handshake por CADA comando
```
cada comando del juego abre su propia conexión con un handshake, se ejecuta y cierra el socket. Se ve que el saludo TCP se repite en cada operación, lo que añade latencia, pero también que el socket se libera enseguida, dejando libres los recursos.

Este otro diagrama muestra la **conexión persistente**:


```mermaid
sequenceDiagram
    participant C as Cliente
    participant S as Servidor
    C->>S: handshake TCP (una sola vez)
    Note over C,S: socket abierto toda la partida
    C->>S: SELECT_CELL
    S->>C: respuesta
    C->>S: MARK_CELL
    S->>C: respuesta
    C->>S: GET_BOARD
    S->>C: respuesta
    C->>S: cerrar socket (al final)
    Note over C,S: Un solo handshake, pero el hilo<br/>queda ocupado toda la partida
```
aquí el handshake se hace una sola vez y el socket permanece abierto mientras se ejecutan todos los comandos, cerrándose solo al final de la partida. Se aprecia la ventaja en latencia (un único saludo) pero también el costo: el socket —y en este diseño, el hilo del pool— queda reservado para ese cliente durante toda la sesión.
### Diagrama Bonus de como funciona el proyecto con sus Subproyectos, CLIENTE y SERVER
```mermaid
flowchart TD
    subgraph CLIENTE
        MC[MainClient<br/>menu + Scanner]
        REND[printBoard<br/>renderizador ANSI]
        TCPC[BuscaminasTCPClient<br/>sendRequest]
        MC --> TCPC
        TCPC --> REND
    end

    NET{{Socket TCP<br/>puerto 12345<br/>JSON + n}}

    subgraph SERVIDOR
        CTRL[TCPController<br/>+ ThreadPool 5 hilos]
        SERV[ServicesImpl]
        MODEL[(BoardGame<br/>synchronized)]
        CTRL --> SERV
        SERV --> MODEL
    end

    TCPC -->|Request JSON| NET
    NET -->|Request JSON| CTRL
    CTRL -->|Response JSON| NET
    NET -->|Response JSON| TCPC
```
### 5. Declaración y Reflexión sobre IAG: Declare explícitamente que herramientas de Inteligencia Artificial Generativa utilizo durante el desarrollo del laboratorio, que prompts empleo para formular la solución del cliente o depurar el servidor, y como valido que el código propuesto por la IA cumpliera las reglas del protocolo sin introducir vulnerabilidades.

el modelo o el agente de IA que utilice fue Claude Pro con el modelo Opus esfuerzo alto; antes que nada le mande el pdf del LAB, para que entendiera el objectivo del proyecto como tal, ademas, pues me base en el para hacer la estructura de los dos subproyectos, cliente y server, ya que es algo que no habia hecho, le pedi ayuda en lo de grawdle, pues para saber si estaban bien sus respuestas lei el enunciado para ver si cumplia con los requisitos de la rubrica; en la parte del server si le pedi explicitamente que me ayudara a acomodar la parte del server ya que esta aun incompleto, para asi despues hacerle con cliente menos en la mainClient, donde  yo le mandaba propuestas de codigo donde me corregia y hacia un nuevo codigo en base al mio, tambien le pedi comentarios para entender bien el funcionamiento del proyecto y demas.

el uso de IA en mi proyecto fue muy colaborativo, fue de trabajar a la par, y muestra de eso fue como hice las respuestas del cuestionario; sin mas que decir compartire el chat con claude para evidenciar lo que estoy diciendo

https://claude.ai/share/299624d3-a31c-41f8-a554-086bb6818fbd