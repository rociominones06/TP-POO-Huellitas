# Huellitas 🐾

**Universidad:** UADE  
**Carrera:** Ingeniería en Informática  
**Materia:** Paradigma Orientado a Objetos  
**Curso y turno:** Miércoles - Tarde  
**Docente:** Leon Maria Angela  
**Fecha de entrega - Etapa 1:** 25/10/2026  
**Fecha de entrega - Etapa 2:** 04/11/2026

## Integrantes

- Miñones, Rocío — Legajo: 1215772
- Ortiz, Dolores — Legajo: 1215275
- Testone, Valentina — Legajo: 1185463

## Propuesta B

**Nombre:** Clínica Veterinaria

## Descripción del sistema

**Huellitas** es una aplicación de escritorio para organizar la gestión cotidiana de una clínica veterinaria. Está desarrollada en Java con una interfaz gráfica Swing y reúne en una misma ventana la información de clientes y mascotas, los turnos, la atención veterinaria, el inventario y la facturación. Desde el menú lateral se puede cambiar de módulo; el inicio muestra indicadores de dueños, mascotas, veterinarios y productos registrados.

El sistema permite mantener los datos relacionados entre sí: las mascotas pertenecen a un dueño, sus consultas se registran en una historia clínica y las facturas se emiten asociadas a consultas confirmadas. Los cambios se conservan entre ejecuciones mediante archivos locales, como se detalla en **Datos y notificaciones**.

## Funcionalidades

- **Inicio:** muestra un resumen de la cantidad de dueños, mascotas, veterinarios y productos cargados.
- **Dueños y mascotas:** registra dueños con sus datos de contacto y plan de salud; permite buscarlos por nombre, apellido o documento, consultar sus mascotas y registrar animales con su especie y datos particulares.
- **Gestión de turnos:** registra turnos para mascotas y veterinarios, contempla la atención de urgencias y permite cancelar turnos o marcar inasistencias.
- **Historias clínicas:** consulta el historial de cada mascota y permite registrar consultas con sus datos de atención. Las consultas pueden incluir tratamientos y aplicaciones de vacunas; el detalle del registro se puede consultar desde la pantalla.
- **Inventario y proveedores:** registra productos y proveedores, recibe lotes con cantidades y vencimientos, y permite buscar productos y consultar sus existencias. La tabla informa el stock total, el stock utilizable y si el producto requiere reposición.
- **Facturación:** emite facturas para consultas confirmadas, con selección del medio de pago; permite consultar el detalle y anular una factura mediante una nota de crédito. El cálculo puede contemplar descuentos o recargos según las reglas del sistema.
- **Inicio de sesión:** ofrece cuentas de prueba para acceder a la interfaz. En esta versión, los tres usuarios disponibles comparten las mismas secciones y permisos.

La aplicación es un prototipo académico: trabaja con datos locales, no usa una base de datos y las notificaciones de correo se simulan en la consola.

## Requisitos del Programa

- Java Development Kit (JDK) 11 o posterior. Se necesita el JDK para compilar; tener solo el JRE no alcanza.
- Un sistema operativo con entorno gráfico para abrir la interfaz Swing.
- No se requieren bibliotecas externas ni una base de datos: el proyecto usa clases incluidas en Java.

## Cómo ejecutar

### Desde un IDE

1. Abrir o importar la carpeta raíz del proyecto (la carpeta que contiene `src` y este README).
2. Configurar el JDK 11 o posterior como SDK/JDK del proyecto.
3. Marcar `src` como raíz de código fuente si el IDE no lo detecta automáticamente. Mantener la codificación del proyecto en UTF-8.
4. Abrir `src/VistaMain/Main.java` y ejecutar el método `main` (por ejemplo, con **Run** en IntelliJ IDEA o **Run As > Java Application** en Eclipse).

### Desde una terminal

Ejecutar los comandos desde la carpeta raíz del proyecto. Primero, comprobar que Java y el compilador estén disponibles:

```text
java -version
javac -version
```

En macOS o Linux, compilar y ejecutar:

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name '*.java')
java -cp out VistaMain.Main
```

En Windows PowerShell, compilar y ejecutar:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$archivosJava = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $archivosJava
java -cp out VistaMain.Main
```

La compilación coloca los archivos `.class` en `out`. Si se modifica el código, volver a ejecutar los comandos de compilación antes de iniciar el programa.


## Cómo ingresar a la interfaz

1. Ejecutar la clase `Main`, ubicada en `src/VistaMain/Main.java`.
2. En la pantalla de inicio de sesión, ingresar con cualquiera de estas cuentas de prueba:

   |   Usuario   | Contraseña |
   |-------------|------------|
   | `recepcion` |   `1234`   |
   |    `vet`    |   `1234`   |
   |   `admin`   |   `1234`   |

3. Una vez iniciada la sesión, se abrirá la ventana principal. En el menú de la izquierda, hacer clic en **Dueños y mascotas** para acceder a esa sección.
4. Para volver al inicio de sesión, seleccionar **Cerrar Sesión** al final del menú lateral.

Estas credenciales están definidas en el código como datos de prueba; no representan un sistema de autenticación para producción. Los tres roles pueden ver las mismas secciones.

## Datos y notificaciones

- La información de la clínica se guarda en `datos/clinica.dat`, en formato binario. La ruta es relativa a la carpeta desde la que se inicia el programa; por eso, ejecutar desde la raíz del proyecto mantiene los datos en la carpeta `datos` del proyecto.
- Al cerrar la ventana principal, el sistema guarda los cambios. Cerrarla con el botón de cierre de la ventana permite que se realice ese guardado.
- Si no existe un archivo de datos, el programa crea una clínica e incorpora datos iniciales de prueba. Si ya hay datos, los carga y conserva entre ejecuciones.
- Los incidentes se registran en `datos/incidentes.log` cuando corresponde. Si un archivo de datos no se puede leer, el sistema lo aparta como `datos/clinica.dat.corrupto` y muestra un error; puede ser necesario restaurar una copia compatible o iniciar con datos nuevos.
- Las notificaciones por correo se imprimen en la consola como demostración. El programa no configura ni envía correos reales.

## Estructura del código

- `src/VistaMain`: punto de entrada y pantallas Swing.
- `src/PersonasMascota`: personas, veterinarios y tipos de mascotas.
- `src/GestionTurnos`: turnos y planificación.
- `src/AtencionHistoriaClinica`: consultas, vacunas, tratamientos e historias clínicas.
- `src/InventarioProveedores`: productos, lotes y proveedores.
- `src/FacturacionNotificaciones`: facturación, medios de pago y notificaciones.
- `src/GeneralPersistencia`: clínica y guardado/carga de datos.
- `src/Excepciones`: excepciones del dominio.
