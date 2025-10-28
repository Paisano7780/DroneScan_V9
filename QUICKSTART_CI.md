# 🚀 Inicio Rápido: CI/CD con GitHub Actions + Firebase Test Lab

## ✅ Lo que se Instaló

### 1. **GitHub Actions Workflow** (`.github/workflows/android-ci.yml`)
- Compilación automática en cada push/PR
- Tests unitarios en JVM
- Tests instrumentados en Android
- Integración con Firebase Test Lab

### 2. **Tests Básicos**
- `BasicUnitTest.kt`: Tests sin dependencias Android
- `BasicInstrumentedTest.kt`: Tests que requieren Android (emulador/dispositivo)

### 3. **Documentación**
- `.github/FIREBASE_SETUP.md`: Guía completa de configuración

## 🎯 Próximos Pasos

### Para Firebase Test Lab (Opcional pero Recomendado)

1. **Crear proyecto Firebase:**
   - Ve a https://console.firebase.google.com/
   - Crea proyecto: `dronescan-v9`

2. **Habilitar Test Lab:**
   - Firebase Console → Test Lab
   - Activa la API

3. **Crear Service Account:**
   - Project Settings → Service Accounts
   - Genera nueva private key (descarga JSON)

4. **Configurar Secret en GitHub:**
   - Tu repo → Settings → Secrets → Actions
   - Nuevo secret: `FIREBASE_SERVICE_ACCOUNT`
   - Pega el contenido del JSON

5. **Crear bucket de resultados:**
   ```bash
   # En Google Cloud Console
   # Bucket: dronescan-v9-test-results
   # Región: us-central1
   ```

## 📊 Ver Resultados

### En GitHub:
- Ve a la pestaña **"Actions"** de tu repositorio
- Click en el workflow más reciente
- Descarga artefactos (APKs, resultados)

### Comandos Locales:

```bash
# Tests unitarios
./gradlew testDebugUnitTest

# Tests instrumentados (requiere emulador o dispositivo)
./gradlew connectedDebugAndroidTest

# Compilar todo
./gradlew assembleDebug assembleDebugAndroidTest
```

## ⚠️ Nota Importante

**Sin configurar Firebase**, el workflow funcionará EXCEPTO el job `firebase-test`.
Esto es intencional y no causará errores. Los jobs de `build` y tests unitarios
funcionan sin Firebase.

## 🔗 Enlaces Útiles

- [Documentación Completa](.github/FIREBASE_SETUP.md)
- [Firebase Console](https://console.firebase.google.com/)
- [GitHub Actions](https://github.com/features/actions)
- [Firebase Test Lab](https://firebase.google.com/docs/test-lab)

## 💡 Tips

- El workflow corre automáticamente en cada push a `main` o `develop`
- Los artefactos (APKs) se guardan por 7 días
- Los resultados de Firebase se guardan por 30 días
- Puedes descargar los APKs directamente desde la página del workflow
