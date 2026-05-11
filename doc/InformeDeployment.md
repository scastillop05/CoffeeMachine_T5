# Informe de Deployment
## Sistema CoffeeMach — Tarea 5

---

**Autores:**  
Juan Jose Vidarte, Sebastian Castillo, Santiago Zapata, Juan David Salazar, Jose Miguel Armas

**Repositorio:** https://github.com/scastillop05/CoffeeMachine_T5  
**Rama:** main

---

## Diagrama de Deployment

![Diagrama de Deployment UML](Delpoyment_Diagram.pdf)

---

## 1. Descripción general del sistema

CoffeeMach es un sistema distribuido de gestión de máquinas expendedoras de café. Su arquitectura está compuesta por cinco módulos que se comunican entre sí mediante el middleware **ZeroC ICE**, a través de interfaces definidas en el archivo Slice `CoffeMach.ice`.

| Módulo | Descripción |
|---|---|
| PostgreSQL | Base de datos relacional central. Almacena información de máquinas, operadores, alarmas, recetas y ventas. |
| ServidorCentral | Servidor ICE. Coordina operadores, máquinas asignadas, registro de alarmas y ventas. |
| CoffeeMach | Interfaz gráfica de la máquina expendedora. Procesa ventas, gestiona ingredientes y emite alarmas. |
| cmLogistics | Cliente de consola para operadores de logística. Consulta alarmas activas y envía órdenes de abastecimiento. |
| bodegaCentral | Aplicación de escritorio para gestión del inventario físico de bodega. Opera de forma independiente. |

---

## 2. Implementación — Segunda parte

### 2.1 Módulo cmLogistics

Este módulo fue implementado desde cero como cliente de consola ICE. Su propósito es permitir a un operador de logística autenticarse, consultar el estado de las máquinas asignadas y resolver alarmas de desabastecimiento.

**Flujo de operación:**

1. El comunicador ICE se inicializa con la configuración definida en `CmLogistic.cfg`.
2. Se crea un proxy de tipo `ServicioComLogisticaPrx` apuntando al ServidorCentral en el puerto 12345.
3. Se crea un proxy de tipo `ServicioAbastecimientoPrx` apuntando a la CoffeeMach en el puerto 12346.
4. Se lanza `ConsolaLogistica.iniciar()`, que ejecuta el siguiente flujo interactivo:
   - Autenticación mediante `inicioSesion(codOperador, password)`.
   - Menú de opciones: ver máquinas asignadas, ver alarmas activas, resolver alarma, salir.
   - La resolución de una alarma invoca `abastecer(codMaquina, idAlarma)` directamente sobre la CoffeeMach correspondiente.

**Archivos creados:**

| Archivo | Descripción |
|---|---|
| `cmLogistics/src/main/java/CmLogistics.java` | Punto de entrada. Inicializa ICE y lanza la consola. |
| `cmLogistics/src/main/java/logistica/ConsolaLogistica.java` | Lógica del menú interactivo. |
| `cmLogistics/src/main/resources/CmLogistic.cfg` | Configuración de endpoints ICE. |

### 2.2 Módulo bodegaCentral

Este módulo fue implementado desde cero como aplicación de escritorio Swing independiente. Gestiona el inventario físico de la bodega sin requerir conectividad ICE.

**Arquitectura interna:**

- `BodegaImpl`: implementa la interfaz `Bodega`. Mantiene el inventario en estructuras `LinkedHashMap`. Stock inicial configurado según los datos de la base de datos.
- `InventarioImpl`: implementa la interfaz `Inventario`. Delega en `BodegaImpl` para reabastecer existencias con cantidades predefinidas.
- `Interfaz`: ventana principal `JFrame` con diez botones de operación que invocan métodos de `BodegaImpl` e `InventarioImpl`. El resultado de cada operación se muestra en un área de texto.

**Stock inicial:**

| Categoría | Ítem | Cantidad |
|---|---|---|
| Ingredientes | Agua | 10.000 |
| Ingredientes | Cafe | 5.000 |
| Ingredientes | Azucar | 5.000 |
| Ingredientes | Vaso | 200 |
| Monedas | $100 | 100 |
| Monedas | $200 | 100 |
| Monedas | $500 | 100 |
| Suministros | Kit de reparación | 5 |

**Archivos creados o modificados:**

| Archivo | Descripción |
|---|---|
| `bodegaCentral/src/main/java/BodegaCentral.java` | Punto de entrada. Lanza la interfaz gráfica. |
| `bodegaCentral/src/main/java/bodega/BodegaImpl.java` | Implementación de la interfaz `Bodega`. |
| `bodegaCentral/src/main/java/mantenimientoExistencias/InventarioImpl.java` | Implementación de la interfaz `Inventario`. |
| `bodegaCentral/src/main/java/guiInventario/Interfaz.java` | Ventana principal Swing. |

---

## 3. Configuración para pruebas locales

Los siguientes archivos fueron modificados para adaptar el sistema a un entorno local. Para el despliegue en laboratorio deben restaurarse los valores originales.

| Archivo | Parámetro | Valor original | Valor local |
|---|---|---|---|
| `ServidorCentral/src/main/resources/server.cfg` | Puerto BD | 5430 | 5432 |
| `ServidorCentral/src/main/resources/server.cfg` | Password BD | cofmachpwd | cofmachu |
| `coffeeMach/src/main/resources/coffeMach.cfg` | IPs de máquinas | 10.147.19.x | localhost |
| `scripts/postgres/coffeemach-user.sql` | Password usuario BD | cofmachpwd | cofmachu |

---

## 4. Procedimiento de deployment en laboratorio

El deployment se realizó de forma remota desde los equipos personales de los estudiantes, conectados al laboratorio mediante la red virtual **ZeroTier (IAsLab3 — 159924d630d54382)**.

El orden de despliegue debe respetarse estrictamente: Base de datos → ServidorCentral → CoffeeMach → cmLogistics → bodegaCentral.

### Infraestructura

| Componente      | Máquina | IP ZeroTier   | Puerto |
|-----------------|---------|---------------|--------|
| Base de datos   | x104m07 | 10.147.17.107 | 5432   |
| ServidorCentral | x104m05 | 10.147.17.105 | 12345  |
| coffeeMach M1   | x104m01 | 10.147.17.101 | 12346  |
| coffeeMach M2   | x104m02 | 10.147.17.102 | 12346  |
| cmLogistics     | x104m06 | 10.147.17.106 | 12347  |
| bodegaCentral   | x104m08 | 10.147.17.108 | —      |

**SO nodos:** Ubuntu 24.04 LTS | **Java:** OpenJDK 11 | **ICE:** 3.7.9 en `/opt/Ice-3.7.9` | **Gradle:** 8.12

### Prerequisitos en el equipo de cada estudiante

1. Instalar **ZeroTier** y unirse a la red `159924d630d54382`.
2. Cliente SSH: Windows Terminal / PuTTY (Windows) o Terminal nativo (macOS).
3. Para componentes con GUI: servidor X11 — **VcXsrv o Xming** (Windows) / **XQuartz** (macOS).

---

### 4.1 Base de datos — Sebastian Castillo (x104m07)

**1. Conectarse y acceder como postgres:**
```bash
ssh swarch@10.147.17.107
su - postgres
```

**2. Crear usuario y base de datos:**
```bash
psql -c "CREATE USER cofmachu WITH PASSWORD 'cofmachpwd';"
psql -c "CREATE DATABASE coffeemachine OWNER cofmachu;"
psql -c "GRANT CONNECT ON DATABASE coffeemachine TO cofmachu;"
```

**3. Clonar repo y cargar esquema:**
```bash
git clone https://github.com/scastillop05/CoffeeMachine_T5.git
psql -h localhost -U cofmachu -d coffeemachine -f CoffeeMachine_T5/scripts/postgres/coffeemach-ddl.sql
psql -h localhost -U cofmachu -d coffeemachine -f CoffeeMachine_T5/scripts/postgres/coffeemach-inserts.sql
```

**4. Habilitar conexiones externas:**

En `/etc/postgresql/16/main/pg_hba.conf` agregar al final:
```
host coffeemachine cofmachu 10.147.17.0/24 md5
```
En `/etc/postgresql/16/main/postgresql.conf` cambiar:
```
listen_addresses = '*'
```

**5. Reiniciar PostgreSQL:**
```bash
/usr/bin/pg_ctlcluster 16 main restart
```

---

### 4.2 ServidorCentral — Jose Miguel Armas (x104m05)

```bash
ssh -X swarch@10.147.17.105
git clone https://github.com/scastillop05/CoffeeMachine_T5.git
cd CoffeeMachine_T5/coffeemach
gradle :ServidorCentral:jar
java -jar ServidorCentral/build/libs/ServidorCentral.jar
```

**Verificación:** Se abre la ventana "Interfaz Recetas" con ingredientes y recetas cargados desde la BD.

---

### 4.3 coffeeMach instancia 1 — Juan David Salazar (x104m01)

```bash
ssh -X swarch@10.147.17.101
git clone https://github.com/scastillop05/CoffeeMachine_T5.git
cd CoffeeMachine_T5/coffeemach
gradle :coffeeMach:jar
java -jar coffeeMach/build/libs/coffeeMach.jar
```

**Verificación:** Se abre la ventana "Maquina de Cafe" con insumos, recetas y monedas cargados.

---

### 4.4 coffeeMach instancia 2 — Juan Jose Vidarte (x104m02)

```bash
ssh -X swarch@10.147.17.102
git clone https://github.com/scastillop05/CoffeeMachine_T5.git
cd CoffeeMachine_T5/coffeemach
gradle :coffeeMach:jar
java -jar coffeeMach/build/libs/coffeeMach.jar
```

---

### 4.5 cmLogistics y bodegaCentral — Santiago Zapata (x104m06 / x104m08)

**cmLogistics:**
```bash
ssh swarch@10.147.17.106
git clone https://github.com/scastillop05/CoffeeMachine_T5.git
cd CoffeeMachine_T5/coffeemach
gradle :cmLogistics:jar
java -jar cmLogistics/build/libs/cmLogistics.jar
# Credenciales: operador 1 / contraseña 1123
```

**bodegaCentral:**
```bash
ssh -X swarch@10.147.17.108
git clone https://github.com/scastillop05/CoffeeMachine_T5.git
cd CoffeeMachine_T5/coffeemach
gradle :bodegaCentral:jar
java -jar bodegaCentral/build/libs/bodegaCentral.jar
```

**Verificación:** Se abre la ventana "Bodega Central - CoffeeMach" con el inventario inicial.

---

## 5. Flujo de comunicación entre componentes

```
[coffeeMach M1]  ──ICE 12345──►  [ServidorCentral]  ──JDBC 5432──►  [PostgreSQL]
[coffeeMach M2]  ──ICE 12345──►  [ServidorCentral]
[cmLogistics]    ──ICE 12345──►  [ServidorCentral]
[cmLogistics]    ──ICE 12346──►  [coffeeMach M1/M2]
[bodegaCentral]  (standalone, sin conexión ICE)
```

**Descripción del flujo:**

1. **coffeeMach → ServidorCentral (ICE, puerto 12345):** cada instancia de la máquina expendedora se registra con el ServidorCentral al iniciar. Consume los servicios `Alarmas`, `Ventas` y `Recetas` expuestos por el servidor.

2. **ServidorCentral → PostgreSQL (JDBC, puerto 5432):** el ServidorCentral persiste y consulta toda la información del negocio: recetas, ingredientes, operadores, máquinas asignadas, alarmas y ventas.

3. **cmLogistics → ServidorCentral (ICE, puerto 12345):** el operador de logística consulta al ServidorCentral las máquinas asignadas (`verMaquinasAsignadas`) y las alarmas activas (`verAlarmas`) mediante el servicio `logistica`.

4. **cmLogistics → coffeeMach (ICE, puerto 12346):** al resolver una alarma, cmLogistics invoca directamente el método `abastecer(codMaquina, idAlarma)` sobre el proxy ICE de la máquina correspondiente, sin pasar por el ServidorCentral.

5. **bodegaCentral:** opera de forma completamente independiente. Gestiona el inventario físico local de la bodega en memoria y no requiere conexión ICE ni acceso a la base de datos.

---

## 6. Resultados de las pruebas

### 6.1 Entorno local (macOS, Java 22, PostgreSQL 14)

| N.° | Prueba | Resultado |
|---|---|---|
| 1 | Creación de base de datos, tablas y datos iniciales | Aprobado |
| 2 | ServidorCentral arranca y escucha en puerto 12345 | Aprobado |
| 3 | CoffeeMach carga GUI con recetas e ingredientes | Aprobado |
| 4 | cmLogistics: autenticación con operador 1 / contraseña 1123 | Aprobado |
| 5 | cmLogistics: listado de máquinas asignadas (3 máquinas) | Aprobado |
| 6 | cmLogistics: listado de alarmas activas (4 alarmas) | Aprobado |
| 7 | cmLogistics: resolución de alarma, llamada abastecer(1,1) recibida en CoffeeMach | Aprobado |
| 8 | bodegaCentral: GUI inicia con stock inicial correcto | Aprobado |

### 6.2 Entorno de laboratorio (Ubuntu 24.04, Java 11, PostgreSQL 16, ZeroTier)

| N.° | Prueba | Resultado |
|---|---|---|
| 1 | PostgreSQL acepta conexiones externas desde subred 10.147.17.0/24 | Aprobado |
| 2 | ServidorCentral (104m03) conecta a BD (104m08) y carga recetas | Aprobado |
| 3 | CoffeeMach (104m01) conecta a ServidorCentral y carga GUI | Aprobado |
| 4 | cmLogistics (104m03) autentica operador y lista máquinas/alarmas | Aprobado |
| 5 | cmLogistics resuelve alarma: comando abastecer llega a coffeeMach | Aprobado |
| 6 | bodegaCentral (104m08) inicia GUI con inventario correcto | Aprobado |

---

## 7. Historial de commits relevantes

| Hash    | Descripción |
|---------|-------------|
| e42ff87 | config: IPs reasignadas a maquinas disponibles en lab |
| 1e73a5c | config: iceHome para lab Ubuntu y password BD corregido |
| 448a296 | Organizacion de carpetas |
| 9eb0ade | feat: implementar cmLogistics y bodegaCentral para resolución de alarmas |
| 67d1054 | Codigo base del sistema CoffeeMach |
