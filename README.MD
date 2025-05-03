# 🌍 GeoApp

**GeoApp** es una aplicación de mapas interactivos desarrollada en Android que permite a los usuarios explorar ubicaciones, guardar puntos favoritos y agregar alertas personalizadas en el mapa. Utiliza **Mapbox** para la visualización y ofrece una experiencia dinámica con múltiples estilos de mapa y animaciones.

---

## ✨ Características principales

- 🗺️ **Exploración de mapas**: Navega por el mapa utilizando Mapbox con una interfaz fluida.
- ⭐ **Puntos favoritos**: Guarda ubicaciones importantes para acceder fácilmente después.
- 🚨 **Alertas animadas**: Agrega puntos de alerta con animaciones personalizadas para destacar zonas importantes.
- 🎨 **Cambio de estilo de mapa**: Alterna entre estilos como callejero, satélite, oscuro, entre otros.
- ✋ **Gestos interactivos**: Soporte para toques y pulsaciones largas directamente en el mapa.
- 📍 **Gestión de ubicación**: Ubica y centra el mapa en tu posición actual con un solo toque.

---

## 🛠️ Tecnologías utilizadas

- **Lenguaje**: Kotlin
- **Frameworks y librerías**:
   - Mapbox SDK
- **Persistencia**: SQLite con Room
- **Animaciones**: ValueAnimator


---

## 🚀 Instalación y ejecución

1. Clona este repositorio:
   ```bash
   git clone https://github.com/cristianco7/geoapp.git
   ```
2. Abre el proyecto en **Android Studio**.
3. Agrega tu token de Mapbox en el archivo `mapbox_access_token.xml` dentro de `res/values/`:
   ```
   MAPBOX_ACCESS_TOKEN=tu_token_de_mapbox
   ```
4. Sincroniza las dependencias de Gradle.
5. Ejecuta la aplicación en un emulador o dispositivo físico.

---

## 📱 Uso básico

- **Explorar el mapa**: Simplemente abre la app y navega libremente.
- **Agregar favoritos**: Haz una pulsación larga sobre el mapa.
- **Agregar alertas**: Toca una ubicación y selecciona "Agregar alerta".
- **Cambiar el estilo**: Usa el botón de estilo para cambiar la apariencia del mapa.
- **Ver favoritos**: Accede al menú para ver la lista de ubicaciones guardadas.

---

## 📁 Estructura del proyecto

- `MainActivity.kt`: Actividad principal que maneja la interfaz y eventos.
- `MapManager.kt`: Encapsula la lógica de visualización y estilos del mapa.
- `LocationManager.kt`: Maneja la ubicación actual del usuario.
- `UIUtils.kt`: Funciones auxiliares para la interfaz.
- `AppDatabase.kt`: Configuración de la base de datos local usando Room.

