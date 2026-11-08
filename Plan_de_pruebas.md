# Plan de Pruebas y Resultados

Pruebas del sistema de gestión de clínica veterinaria **Huellitas**. Se contemplan dos niveles de verificación:

1. **Pruebas unitarias:** verifican de forma automatizada y aislada las reglas de negocio críticas del modelo de dominio utilizando el framework **JUnit 5**.
2. **Pruebas funcionales:** verifican los principales casos de uso mediante la interfaz gráfica Java Swing, simulando el flujo de trabajo habitual del personal de la clínica.

---

## 1. Entorno y Parámetros de Prueba

| Parámetro | Valor |
|---|---|
| Sistema | Huellitas – Clínica Veterinaria |
| Propuesta | Propuesta B |
| Fecha de ejecución | Noviembre 2026 |
| Lenguaje | Java (JDK 11+) |
| Framework de Pruebas Unitarias | JUnit 5 (Jupiter 5.14.0) |
| Interfaz Gráfica | Java Swing (`VistaMain.Main`) |
| Persistencia | Archivo binario serializado `datos/clinica.dat` |
| Control de Accesos | Roles diferenciados (`Recepcionista`, `Veterinario`, `Administrador`) |
| Credenciales de prueba | `recepcion` / `1234`, `vet` / `1234`, `admin` / `1234` |

---

## 2. Pruebas Unitarias Automatizadas (JUnit 5)

Las pruebas unitarias se implementaron en la clase `test/PruebasUnitarias.java` y validan las reglas de negocio críticas del dominio en forma aislada e independiente de la interfaz gráfica.

### Casos de Prueba Unitarios Ejecutados

| Test ID | Método de Prueba | Regla de Negocio / Módulo | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|---|:---:|
| **TU-01** | `testFacturacionDescuentoEfectivo()` | **RN5.2 (Facturación):** 10% de descuento en pagos en efectivo | Subtotal de $10.000 se reduce exactamente a $9.000 | Total calculado: $9.000,00 | **APROBADO** |
| **TU-02** | `testFacturacionRecargoCredito()` | **RN5.4 (Facturación):** 8% de recargo con tarjeta de crédito | Subtotal de $10.000 se incrementa a $10.800 | Total calculado: $10.800,00 | **APROBADO** |
| **TU-03** | `testConsumoStockCriterioFEFO()` | **RN4.4 y RN4.5 (Inventario):** Descuento bajo criterio FEFO | Descuenta primero del lote utilizable más próximo a vencer | Stock del lote más próximo reducido de 5 a 2; lote lejano intacto con 5 | **APROBADO** |
| **TU-04** | `testControlAusenciasYExigenciaSena()` | **RN1.6 (Turnos):** Ausencias consecutivas y seña | 3 inasistencias consecutivas activan seña obligatoria; resetea al concurrir | Seña activada en la 3ra ausencia y desactivada al resetear | **APROBADO** |
| **TU-05** | `testRestriccionMedicarSinPeso()` | **RN2.7 (Atención):** Restricción de medicar sin peso | Lanza `ReglaNegocioException` si se receta con peso 0.0 | Excepción capturada correctamente, prescripción bloqueada | **APROBADO** |
| **TU-06** | `testAnulacionFacturaConNotaCredito()` | **RN5.7 y RN5.8 (Facturación):** Inmutabilidad y Nota de Crédito | Emisión inmutable; anulación genera Nota de Crédito; bloquea doble anulación | Factura anulada con Nota de Crédito vinculada; 2da anulación rechazada | **APROBADO** |

> **Resultado Unitario:** 6 pruebas ejecutadas, **6 superadas (100%)**, 0 fallas. Tiempo total: 79 ms.

---

## 3. Pruebas Funcionales (Interfaz de Usuario Swing)

Las pruebas funcionales se ejecutaron sobre la interfaz Swing de la aplicación, cubriendo los 10 casos de uso principales.

### CU-01: Autenticación y Control de Acceso

| Caso | Acción del Usuario | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|:---:|
| PF-01 | Ingresar con usuario `recepcion` y contraseña `1234` | Inicia sesión y muestra pestañas operativas (Turnos, Facturación, Inventario) | Acceso concedido con vista adaptada al rol Recepcionista | **APROBADO** |
| PF-02 | Ingresar con usuario `vet` y contraseña `1234` | Inicia sesión con pestañas clínicas (Historias Clínicas, Turnos) | Acceso concedido con vista médica | **APROBADO** |
| PF-03 | Ingresar con usuario `admin` y contraseña `1234` | Inicia sesión con acceso total a todos los módulos | Acceso concedido con todas las pestañas activas | **APROBADO** |
| PF-04 | Ingresar contraseña incorrecta | Deniega el acceso e informa error | Muestra diálogo de credenciales inválidas | **APROBADO** |

### CU-02: Registrar y Consultar Dueños

| Caso | Acción del Usuario | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|:---:|
| PF-05 | Registrar dueño con datos válidos | Queda guardado y aparece en el listado | Dueño dado de alta y seleccionable en combos | **APROBADO** |
| PF-06 | Intentar registrar dueño con DNI duplicado | El sistema impide el registro duplicado | Se emite alerta de documento ya registrado | **APROBADO** |
| PF-07 | Buscar dueño por DNI | Muestra los datos personales correspondientes | Datos cargados en pantalla correctamente | **APROBADO** |
| PF-08 | Consultar listado de dueños | Lista a todos los clientes registrados | Tabla actualizada con los datos persistidos | **APROBADO** |

### CU-03: Registrar y Administrar Mascotas

| Caso | Acción del Usuario | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|:---:|
| PF-09 | Registrar mascota vinculada a un dueño | Mascota creada y asociada a su dueño | Mascota visible en la ficha del dueño | **APROBADO** |
| PF-10 | Registrar distintas especies (perro, gato, conejo, ave) | Registra atributos específicos de cada especie | Datos específicos guardados según jerarquía | **APROBADO** |
| PF-11 | Consultar ficha de mascota | Visualiza datos clínicos y especie | Ficha completa desplegada | **APROBADO** |
| PF-12 | Actualizar peso de mascota | Nuevo peso reflejado en la ficha | Peso actualizado en memoria y vista | **APROBADO** |
| PF-13 | Transferir mascota a otro dueño | Se reasigna el titular y se desvincula del anterior | Dueño actualizado en la ficha de la mascota | **APROBADO** |

### CU-04: Gestión de Turnos y Ausencias

| Caso | Acción del Usuario | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|:---:|
| PF-14 | Asignar turno con veterinario disponible | Turno reservado con fecha y horario | Turno listado en la grilla horaria | **APROBADO** |
| PF-15 | Asignar turno fuera del rango horario laboral | El sistema rechaza la asignación | Mensaje de indisponibilidad horaria emitido | **APROBADO** |
| PF-16 | Asignar dos turnos superpuestos al mismo veterinario | Evita superposiciones | Mensaje de conflicto de horario mostrado | **APROBADO** |
| PF-17 | Reprogramar turno a nuevo horario disponible | Turno modificado con nueva fecha/hora | Grilla actualizada con el nuevo horario | **APROBADO** |
| PF-18 | Cancelar turno | Estado cambia a Cancelado | Turno marcado como cancelado | **APROBADO** |
| PF-19 | Marcar turno como atendido | Estado cambia a Atendido | Turno marcado atendido y listo para consulta | **APROBADO** |
| PF-20 | Marcar ausencia ("Marcar Ausente") | Incrementa ausencias del dueño; si llega a 3 exige seña | Contador de inasistencias incrementado; alerta de seña emitida | **APROBADO** |
| PF-21 | Registrar urgencia médica | Atiende con veterinario de guardia | Urgencia gestionada con prioridad | **APROBADO** |

### CU-05: Consultas e Historias Clínicas

| Caso | Acción del Usuario | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|:---:|
| PF-22 | Registrar consulta con motivo, peso y diagnóstico | Consulta creada y agregada a la Historia Clínica | Consulta registrada con ID autoincremental | **APROBADO** |
| PF-23 | Agregar tratamiento farmacológico a la consulta | Queda asociado medicamento, dosis y frecuencia | Tratamiento listado dentro del detalle de consulta | **APROBADO** |
| PF-24 | Consultar historial de una mascota | Muestra consultas y evoluciones previas | Historial cronológico desplegado | **APROBADO** |
| PF-25 | Confirmar y finalizar consulta | Consulta queda cerrada e inmutable | Consulta confirmada y disponible para facturar | **APROBADO** |
| PF-26 | Registrar rectificación médica | Agrega nota rectificatoria sin alterar el registro previo | Nota anexada con firma y fecha | **APROBADO** |

### CU-06: Vacunación y Refuerzos

| Caso | Acción del Usuario | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|:---:|
| PF-27 | Aplicar vacuna compatible con la especie | Registra aplicación, lote y fecha de refuerzo | Aplicación asentada en la Historia Clínica | **APROBADO** |
| PF-28 | Intentar aplicar vacuna a especie no compatible | Rechaza la aplicación | Alerta de incompatibilidad de especie | **APROBADO** |
| PF-29 | Intentar aplicar vacuna antes de la edad mínima | Bloquea la aplicación | Alerta de edad insuficiente emitida | **APROBADO** |
| PF-30 | Consultar alertas de refuerzos | Identifica vacunas próximas a vencer o renovar | Listado de recordatorios generado | **APROBADO** |

### CU-07: Inventario y Criterio FEFO

| Caso | Acción del Usuario | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|:---:|
| PF-31 | Registrar producto con precio y stock mínimo | Producto incorporado al catálogo | Alta confirmada en la tabla de productos | **APROBADO** |
| PF-32 | Recepcionar lote de producto | Lote registrado con fecha de caducidad y cantidad | Stock total incrementado | **APROBADO** |
| PF-33 | Consultar stock utilizable con lotes vencidos | Excluye lotes cuya fecha sea anterior a hoy | Solo se computa el stock de lotes vigentes | **APROBADO** |
| PF-34 | Intentar consumir cantidad mayor al stock disponible | Rechaza la operación por stock insuficiente | Mensaje de stock insuficiente emitido | **APROBADO** |
| PF-35 | Consultar productos bajo stock mínimo | Identifica artículos para orden de compra | Listado de reposición filtrado correctamente | **APROBADO** |

### CU-08: Facturación y Medios de Pago

| Caso | Acción del Usuario | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|:---:|
| PF-36 | Emitir factura desde una consulta | Importa los tratamientos e ítems automáticamente | Factura emitida con líneas detalladas | **APROBADO** |
| PF-37 | Facturar con pago en Efectivo | Aplica 10% de descuento | Importe final reducido en un 10% | **APROBADO** |
| PF-38 | Facturar con Débito | Cobra el total neto exacto | Importe liquidado sin recargos ni descuentos | **APROBADO** |
| PF-39 | Facturar con Tarjeta de Crédito | Aplica 8% de recargo | Importe final incrementado en un 8% | **APROBADO** |
| PF-40 | Facturar a dueño con Plan de Salud | Aplica bonificación del plan si corresponde | Descuento aplicado en el cálculo | **APROBADO** |
| PF-41 | Consultar comprobante por número | Recupera detalle de la factura | Comprobante visualizado con detalle contable | **APROBADO** |
| PF-42 | Filtrar facturas por fecha | Lista comprobantes del período | Grilla filtrada según rango seleccionado | **APROBADO** |
| PF-43 | Anular factura con motivo | Emite Nota de Crédito y anula la factura | Factura marcada como anulada con NC asociada | **APROBADO** |

### CU-09: Notificaciones

| Caso | Acción del Usuario | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|:---:|
| PF-44 | Disparar recordatorio o comprobante | Procesa la notificación y la muestra en consola | Mensaje de log impreso en consola | **APROBADO** |
| PF-45 | Consultar registro de notificaciones | Visualiza el historial de avisos emitidos | Registro de envíos visible | **APROBADO** |

### CU-10: Persistencia de Datos

| Caso | Acción del Usuario | Resultado Esperado | Resultado Obtenido | Estado |
|---|---|---|---|:---:|
| PF-46 | Modificar datos y cerrar la aplicación | Guarda el estado en `datos/clinica.dat` | Archivo binario actualizado al salir | **APROBADO** |
| PF-47 | Iniciar el programa con archivo existente | Recupera todos los datos guardados | Información intacta y disponible | **APROBADO** |
| PF-48 | Iniciar sin archivo previo existente | Regenera los datos iniciales de prueba | Clínica inicializada con datos semilla | **APROBADO** |
| PF-49 | Iniciar con archivo dañado o ilegible | Respalda el archivo corrupto y genera uno nuevo | Recuperación automática sin caída del sistema | **APROBADO** |

---

## 4. Resumen General de Resultados

| Categoría de Pruebas | Casos Planificados | Aprobados (OK) | Fallas | Tasa de Éxito |
|---|:---:|:---:|:---:|:---:|
| **Pruebas Unitarias (JUnit 5)** | 6 | 6 | 0 | **100%** |
| **Pruebas Funcionales (UI Swing)** | 49 | 49 | 0 | **100%** |
| **TOTAL** | **55** | **55** | **0** | **100%** |

### Conclusión
Todas las pruebas unitarias y funcionales planificadas fueron ejecutadas satisfactoriamente, certificando que el sistema **Huellitas** cumple en su totalidad con los requisitos de la Propuesta B, las reglas de negocio críticas y la estabilidad en su interfaz de usuario y persistencia.