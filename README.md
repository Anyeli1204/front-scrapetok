# 📊 MODELO ENTIDAD-RELACIÓN - MICROSERVICIO QA API

## 🎨 DIAGRAMA VISUAL (Mermaid)

```mermaid
erDiagram
    QUEST_AND_ANSWER {
        int id PK
        int user_id "FK ref MS1"
        int admin_id "FK ref MS1"
        enum status "StatusQA"
        text question_description
        date question_date
        time question_hour
        text answer_description "nullable"
        date answer_date "nullable"
        time answer_hour "nullable"
        datetime created_at
    }
    
    SCRAPED_ACCOUNT {
        int id PK
        varchar account_name
        int user_id "FK ref MS1"
        datetime scraped_at
    }
    
    USER_APIFY_CALL_HISTORIAL {
        int id PK
        int user_id "FK ref MS1"
        datetime start_date
        datetime end_date "nullable"
        int execution_time "nullable"
    }
    
    USER_APIFY_FILTERS {
        int id PK
        int historial_id FK
        varchar filter_name "indexed"
        text filter_config "nullable"
        enum status "ApifyRunStatus"
        datetime created_at
    }
    
    STATUS_QA {
        string PENDING
        string ANSWERED
    }
    
    APIFY_RUN_STATUS {
        string COMPLETED
        string FAILED
    }
    
    %% Relaciones principales
    USER_APIFY_CALL_HISTORIAL ||--o{ USER_APIFY_FILTERS : "tiene"
    
    %% Referencias a enums
    QUEST_AND_ANSWER }o--|| STATUS_QA : "usa"
    USER_APIFY_FILTERS }o--|| APIFY_RUN_STATUS : "usa"
    
    %% Referencias externas (sin FK física)
    QUEST_AND_ANSWER }o--|| MS1_USERS : "user_id ref"
    QUEST_AND_ANSWER }o--|| MS1_ADMINS : "admin_id ref"
    SCRAPED_ACCOUNT }o--|| MS1_USERS : "user_id ref"
    USER_APIFY_CALL_HISTORIAL }o--|| MS1_USERS : "user_id ref"
```

---

## 🗂️ ENTIDADES (Formato Texto)

### 1. **QUEST_AND_ANSWER** (Sistema de Preguntas y Respuestas)
```
┌─────────────────────────────────────┐
│           QUEST_AND_ANSWER          │
├─────────────────────────────────────┤
│ PK │ id                    │ INT    │
│    │ user_id              │ INT    │ ← FK (referencia externa a MS1)
│    │ admin_id             │ INT    │ ← FK (referencia externa a MS1)
│    │ status               │ ENUM   │ ← StatusQA (PENDING/ANSWERED)
│    │ question_description │ TEXT   │
│    │ question_date        │ DATE   │
│    │ question_hour        │ TIME   │
│    │ answer_description   │ TEXT   │ ← NULLABLE
│    │ answer_date          │ DATE   │ ← NULLABLE
│    │ answer_hour          │ TIME   │ ← NULLABLE
│    │ created_at           │ DATETIME│
└─────────────────────────────────────┘
```

### 2. **SCRAPED_ACCOUNT** (Cuentas Scrapeadas)
```
┌─────────────────────────────┐
│      SCRAPED_ACCOUNT        │
├─────────────────────────────┤
│ PK │ id           │ INT     │
│    │ account_name │ VARCHAR │
│    │ user_id      │ INT     │ ← FK (referencia externa a MS1)
│    │ scraped_at   │ DATETIME│
└─────────────────────────────┘
```

### 3. **USER_APIFY_CALL_HISTORIAL** (Historial de Llamadas Apify)
```
┌─────────────────────────────────┐
│   USER_APIFY_CALL_HISTORIAL     │
├─────────────────────────────────┤
│ PK │ id              │ INT     │
│    │ user_id         │ INT     │ ← FK (referencia externa a MS1)
│    │ start_date      │ DATETIME│
│    │ end_date        │ DATETIME│ ← NULLABLE
│    │ execution_time  │ INT     │ ← NULLABLE (segundos)
└─────────────────────────────────┘
```

### 4. **USER_APIFY_FILTERS** (Filtros de Configuración Apify)
```
┌─────────────────────────────────┐
│      USER_APIFY_FILTERS         │
├─────────────────────────────────┤
│ PK │ id              │ INT     │
│ FK │ historial_id    │ INT     │ ← FK → USER_APIFY_CALL_HISTORIAL.id
│    │ filter_name     │ VARCHAR │ ← INDEXED
│    │ filter_config   │ TEXT    │ ← NULLABLE
│    │ status          │ ENUM    │ ← ApifyRunStatus (COMPLETED/FAILED)
│    │ created_at      │ DATETIME│
└─────────────────────────────────┘
```

## 🔗 RELACIONES

### Relación Principal:
```
USER_APIFY_CALL_HISTORIAL (1) ─────────── (N) USER_APIFY_FILTERS
     │                                              │
     │ PK: id                                       │ FK: historial_id
     └──────────────────────────────────────────────┘
```

**Tipo**: One-to-Many (1:N)
- Un historial de llamada puede tener múltiples filtros
- Un filtro pertenece a un solo historial
- **CASCADE DELETE**: Si se elimina un historial, se eliminan sus filtros

### Referencias Externas (Sin FK físicas):
```
┌─────────────────┐    user_id    ┌──────────────────────────────┐
│   MICROSERVICIO │ ─────────────→│   QUEST_AND_ANSWER           │
│       1 (MS1)   │               │   SCRAPED_ACCOUNT            │
│   (Usuarios)    │               │   USER_APIFY_CALL_HISTORIAL  │
└─────────────────┘               └──────────────────────────────┘
```

## 📋 ENUMS

### StatusQA
```
┌─────────────┐
│  STATUS_QA  │
├─────────────┤
│ PENDING     │ ← Pregunta sin responder
│ ANSWERED    │ ← Pregunta respondida
└─────────────┘
```

### ApifyRunStatus
```
┌──────────────────┐
│ APIFY_RUN_STATUS │
├──────────────────┤
│ COMPLETED        │ ← Ejecución exitosa
│ FAILED           │ ← Ejecución fallida
└──────────────────┘
```

## 🎯 RESUMEN DE CARDINALIDADES

| Entidad A | Relación | Entidad B | Cardinalidad |
|-----------|----------|-----------|--------------|
| USER_APIFY_CALL_HISTORIAL | tiene | USER_APIFY_FILTERS | 1:N |
| MS1 (Usuarios) | referencia | QUEST_AND_ANSWER | 1:N |
| MS1 (Usuarios) | referencia | SCRAPED_ACCOUNT | 1:N |
| MS1 (Usuarios) | referencia | USER_APIFY_CALL_HISTORIAL | 1:N |
| MS1 (Admins) | referencia | QUEST_AND_ANSWER | 1:N |

## 🔍 ÍNDICES

- **USER_APIFY_FILTERS.filter_name** → Índice para búsquedas rápidas
- **Primary Keys** → Índices automáticos
- **Foreign Keys** → Índices automáticos

## 📝 NOTAS IMPORTANTES

1. **Microservicio**: Este MS2 no maneja usuarios directamente, solo referencias por ID
2. **Cascade Delete**: Eliminar historial elimina sus filtros automáticamente
3. **Timestamps**: Todas las entidades tienen control de fechas/tiempo
4. **Nullable Fields**: Campos opcionales marcados apropiadamente
5. **Enums**: Estados controlados por enumeraciones para consistencia
