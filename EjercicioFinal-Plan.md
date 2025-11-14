# Ejercicio Final: Prototipo RPG Astronómico con Animaciones Combinadas

## Objetivo
- Diseñar un prototipo de videojuego estilo RPG con temática astronómica (viajes entre mundos), demostrando el uso combinado de `AnimatedVisibility`, `AnimatedContent`, `animateColorAsState`, `animateDpAsState`, transiciones `tween` y `spring` para crear una experiencia fluida y atractiva.

## Concepto del Juego
- Ambientación: galaxias y mundos con climas/biomas variables.
- Jugabilidad núcleo: misiones, tareas y diálogos con NPCs.
- Progreso: viajar entre mundos para completar objetivos y desbloquear nuevas zonas.

## Pantallas y Flujo
- Pantalla Inicio: logo animado y transición a menú.
- Mapa Galáctico: selección de mundo con tarjetas animadas; viaje con transición de escena.
- Ciudad/Mundo: HUD con misiones activas; movimiento básico del héroe.
- Diálogo: caja de texto con aparición progresiva y estado de conversación.
- Misiones: lista de misiones con cambios de estado (pendiente → en progreso → completada → fallida).

## Animaciones Clave
- `AnimatedVisibility`: mostrar/ocultar HUD, panel de misiones y caja de diálogo con `fadeIn`/`fadeOut` + `expand/shrink`.
- `AnimatedContent`: transición entre estados de diálogo (intro, respuesta, opciones) y estados de misión (pendiente, progreso, éxito/error) con `fade` y `SizeTransform`.
- `animateColorAsState`: ambientación dinámica (cambio de color de fondo según mundo/bioma) y feedback de botones.
- `animateDpAsState`: movimiento y escala del héroe, desplazamiento de tarjetas del mapa y animación de offset del panel de misiones.
- `spring` vs `tween`: interacción momentánea (spring para rebote suave) y transiciones de escena (tween para control temporal preciso).

## Estados y Arquitectura
- Arquitectura: MVVM con `ViewModel` y `StateFlow`/`MutableState` para UI.
- Estados principales (sealed classes):
  - `SceneState` (GalaxyMap, WorldScene, Dialog, MissionBoard).
  - `DialogState` (Loading, Content, Error, Choices).
  - `MissionState` (Pending, InProgress, Completed, Failed).
- Datos: `Mission(id, title, description, state)`, `World(id, name, themeColor)`.

## Componentes a Implementar
- `GalaxyMapScreen`: grid de mundos con tarjetas animadas; selección realiza transición a `WorldScene`.
- `WorldSceneScreen`: fondo con color animado según mundo; héroe con animaciones de tamaño/posición; HUD con misiones.
- `DialogBox`: contenido animado con `AnimatedContent` y controles de siguiente/opciones.
- `MissionBoard`: lista con `AnimatedVisibility` y transiciones de estado por misión.
- `TravelTransition`: componente visual de viaje (estrellas/warp) usando combinaciones de `fade` y `offset/scale`.

## Plan de Implementación (Commits)
1. Estructurar `ViewModel` y modelos (`World`, `Mission`, sealed states).
2. `GalaxyMapScreen` con tarjetas animadas (`animateDpAsState`, `animateColorAsState`).
3. `WorldSceneScreen` con HUD (`AnimatedVisibility`) y fondo dinámico.
4. `DialogBox` con `AnimatedContent` para `Loading/Content/Error/Choices`.
5. `MissionBoard` con transiciones de estado y `AnimatedContent`.
6. `TravelTransition` entre `GalaxyMap` → `WorldScene`.
7. Pulido de timings y performance (duraciones, damping, evitar recomposición inútil).
8. Tests básicos de estado (ViewModel) y snapshots visuales donde aplique.

## Reglas de Diseño y UX
- Duraciones coherentes: entrada 300–400 ms, salida 200–300 ms.
- Priorizar feedback inmediato del usuario; evitar animaciones largas en acciones frecuentes.
- Mantener jerarquía visual clara: HUD sobre escena, diálogos sobre HUD.

## Performance
- Evitar animar listas grandes simultáneamente.
- Usar `remember` y `derivedStateOf` para minimizar recomposición.
- Medir con `Layout Inspector` y ajustar `tween/spring`.

## Extensiones Futuras
- Partículas simples para estrellas con `Canvas` y animaciones infinitas.
- Sonido y vibración ligera para reforzar feedback.
- Navegación por mundos con guardado de progreso.

## Próximos Pasos
- Implementar `GalaxyMapScreen` y `WorldSceneScreen` siguiendo este plan.
- Integrar `ViewModel` con estados y eventos de navegación.