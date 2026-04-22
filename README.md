# Evidencia IL 2.2 - Jetpack Compose

Este proyecto cumple con el indicador:

> IL 2.2. Crea una interfaz en Jetpack Compose con al menos dos componentes interactivos, validando su usabilidad con un companero o instructor.

## Componentes solicitados

- `Text`: titulos, instrucciones, mensajes y contador.
- `Button`: `Generar saludo`.
- `Image`: logo mostrado en la pantalla principal.
- `TextField`: `OutlinedTextField` para ingresar nombre.

## Componentes interactivos implementados

1. `OutlinedTextField`: entrada del nombre.
2. `Button` (`Generar saludo`): crea mensaje personalizado.
3. `OutlinedButton` (`Otro saludo`): genera variacion del saludo.
4. `Switch` (`Modo divertido`): cambia estilo del saludo.

## Validacion de usabilidad aplicada

- Campo obligatorio: muestra error si esta vacio.
- Minimo de 3 letras.
- Solo letras y espacios.
- Boton principal deshabilitado mientras el nombre no sea valido.
- Mensaje de apoyo en tiempo real en el `TextField`.

## Flujo de prueba sugerido (con companero/instructor)

1. Abrir app y observar pantalla de inicio corta (splash).
2. Intentar saludar sin nombre valido y verificar estado del boton.
3. Escribir nombre valido y generar saludo.
4. Activar/desactivar `Modo divertido` y comparar resultado.
5. Presionar `Otro saludo` para confirmar interaccion continua.

## Resultado esperado

- Interfaz intuitiva con componentes basicos de Compose.
- Al menos dos componentes interactivos funcionando.
- Validacion visible y retroalimentacion clara al usuario.

