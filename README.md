# Achievements (prototipo Android)

Aplicación nativa Android para crear categorías y logros personales.

## Requisitos

- Android Studio reciente
- JDK 17
- Android SDK 36
- Conexión a Internet la primera vez para descargar dependencias Gradle

## Abrir el proyecto

1. Descomprimí `AchievementsApp.zip`.
2. En Android Studio, elegí **Open** y seleccioná la carpeta `AchievementsApp`.
3. Esperá a que termine la sincronización de Gradle.
4. Ejecutá la app en un emulador o dispositivo Android.

## Funcionalidades incluidas en este primer prototipo

- Crear y eliminar categorías.
- Crear y eliminar logros dentro de una categoría.
- Rarezas platino, oro, plata y bronce.
- Estado bloqueado/obtenido controlado manualmente.
- Ordenamiento por nombre, prestigio, estado y fecha de actualización/creación según la pantalla.
- Persistencia local con Room/SQLite.
- Esquema de datos inicial para tareas y campos de imagen personalizada.

## Pendiente para las siguientes iteraciones

- Interfaz para crear y actualizar tareas (checkbox, contador y valor objetivo).
- Edición de categorías y logros.
- Reordenamiento manual por arrastrar y soltar.
- Imágenes predeterminadas definitivas y selección de imágenes propias.
- Exportación e importación segura de `.db` mediante el selector de documentos de Android y carpeta compartida `Achievements`.
- Confirmaciones y copias de seguridad antes de borrar/importar.

Este ZIP es el código fuente del prototipo, no un APK ya compilado. La primera sincronización puede requerir descargar las dependencias de Gradle.
