# 🔥 Configuración de Firebase Test Lab para CI/CD

## Prerrequisitos

1. **Cuenta de Google Cloud Platform (GCP)**
2. **Proyecto de Firebase** (puedes usar uno existente o crear uno nuevo)
3. **Permisos de administrador** en el proyecto

## Paso 1: Crear/Configurar Proyecto Firebase

### Opción A: Crear nuevo proyecto
```bash
# Instalar Firebase CLI (si no lo tienes)
npm install -g firebase-tools

# Login en Firebase
firebase login

# Crear proyecto (o usa uno existente)
firebase projects:create dronescan-v9
```

### Opción B: Usar proyecto existente
- Ve a https://console.firebase.google.com/
- Selecciona tu proyecto existente
- Anota el **Project ID**

## Paso 2: Habilitar Firebase Test Lab

1. Ve a Google Cloud Console: https://console.cloud.google.com/
2. Selecciona tu proyecto
3. Ve a **APIs & Services** → **Library**
4. Busca "**Cloud Testing API**"
5. Click en **Enable**
6. Busca "**Cloud Tool Results API**"
7. Click en **Enable**

## Paso 3: Crear Service Account

```bash
# 1. Ve a Cloud Console
# https://console.cloud.google.com/iam-admin/serviceaccounts

# 2. Click "CREATE SERVICE ACCOUNT"
# - Name: github-actions-firebase
# - Description: Service account for GitHub Actions to run Firebase Test Lab

# 3. Grant roles:
# - Firebase Test Lab Admin
# - Storage Admin (para guardar resultados)
# - Service Account User

# 4. Click "CREATE KEY" → JSON
# - Descarga el archivo JSON
```

### O por CLI:
```bash
# Set project
gcloud config set project YOUR_PROJECT_ID

# Create service account
gcloud iam service-accounts create github-actions-firebase \
  --display-name="GitHub Actions Firebase"

# Grant roles
gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
  --member="serviceAccount:github-actions-firebase@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/firebase.admin"

gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
  --member="serviceAccount:github-actions-firebase@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/storage.admin"

# Create key
gcloud iam service-accounts keys create key.json \
  --iam-account=github-actions-firebase@YOUR_PROJECT_ID.iam.gserviceaccount.com
```

## Paso 4: Crear Bucket de Storage

```bash
# Create bucket for test results
gsutil mb -p YOUR_PROJECT_ID gs://YOUR_PROJECT_ID-test-results

# Set permissions
gsutil iam ch serviceAccount:github-actions-firebase@YOUR_PROJECT_ID.iam.gserviceaccount.com:objectAdmin \
  gs://YOUR_PROJECT_ID-test-results
```

## Paso 5: Configurar GitHub Secrets

1. Ve a tu repositorio en GitHub: https://github.com/Paisano7780/DroneScan_V9
2. Click en **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**

### Secret 1: FIREBASE_SERVICE_ACCOUNT
- **Name**: `FIREBASE_SERVICE_ACCOUNT`
- **Value**: Pega el contenido completo del archivo JSON descargado
  ```json
  {
    "type": "service_account",
    "project_id": "your-project-id",
    "private_key_id": "...",
    "private_key": "...",
    ...
  }
  ```

### Secret 2: GCP_PROJECT_ID
- **Name**: `GCP_PROJECT_ID`
- **Value**: `your-project-id` (ej: `dronescan-v9`)

## Paso 6: Verificar Configuración

```bash
# Test locally (opcional)
gcloud auth activate-service-account --key-file=key.json

# List available devices
gcloud firebase test android models list

# Test with your APK
gcloud firebase test android run \
  --type robo \
  --app ./app/build/outputs/apk/debug/DroneScan_V9-debug-v1.0.apk \
  --device model=Pixel2,version=30,locale=en,orientation=portrait \
  --timeout 5m
```

## Paso 7: Ejecutar Test

Una vez configurados los secrets, simplemente haz push a `main`:

```bash
git add .
git commit -m "🔥 Add Firebase Test Lab configuration"
git push origin main
```

El workflow automáticamente:
1. ✅ Compilará la APK
2. ✅ Ejecutará Robo Test en Firebase (prueba automática de UI)
3. ✅ Guardará logs y resultados
4. ✅ Subirá resultados como artifacts en GitHub

## Ver Resultados

### En GitHub Actions:
- Ve a **Actions** tab
- Click en el workflow ejecutado
- Descarga el artifact "firebase-test-results"

### En Firebase Console:
- Ve a https://console.firebase.google.com/
- Selecciona tu proyecto
- Ve a **Test Lab** → **Test History**

## Troubleshooting

### Error: "Project not found"
```bash
# Verifica que el project ID es correcto
gcloud projects list
```

### Error: "API not enabled"
```bash
# Habilita las APIs necesarias
gcloud services enable testing.googleapis.com
gcloud services enable toolresults.googleapis.com
```

### Error: "Permission denied"
```bash
# Verifica los roles del service account
gcloud projects get-iam-policy YOUR_PROJECT_ID \
  --flatten="bindings[].members" \
  --filter="bindings.members:serviceAccount:github-actions-firebase@*"
```

## Costos

Firebase Test Lab tiene:
- ✅ **15 pruebas físicas/día GRATIS** (Spark plan)
- ✅ **10 pruebas virtuales/día GRATIS**
- 💰 Después: ~$1-5 por hora de testing en dispositivos físicos
- 💰 ~$0.05 por hora en dispositivos virtuales

**Recomendación**: Usa dispositivos virtuales (más baratos) para CI/CD diario.

## Optimizaciones

### Reducir costos:
```yaml
# En el workflow, reduce dispositivos:
--device model=Pixel2,version=30  # Solo 1 dispositivo virtual
--timeout 3m  # Timeout más corto
--no-record-video  # No grabar video
--no-performance-metrics  # No métricas de performance
```

### Solo ejecutar en PRs importantes:
```yaml
if: github.event_name == 'push' && github.ref == 'refs/heads/main'
# O solo en releases:
if: startsWith(github.ref, 'refs/tags/')
```

## Recursos

- [Firebase Test Lab Docs](https://firebase.google.com/docs/test-lab)
- [gcloud CLI Reference](https://cloud.google.com/sdk/gcloud/reference/firebase/test/android)
- [GitHub Actions Firebase Auth](https://github.com/google-github-actions/auth)
