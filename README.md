# **TP - Service gRPC avec Spring Boot : Gestion de Comptes Bancaires**

## 📋 **Description du Projet**
Ce projet implémente un service gRPC pour la gestion de comptes bancaires en utilisant Spring Boot. Le service permet de créer, consulter et gérer des comptes bancaires via des appels RPC (Remote Procedure Call) en utilisant le protocole gRPC.

## 🎯 **Objectifs**
- Implémenter un service gRPC avec Spring Boot
- Définir un contrat de service avec Protobuf
- Gérer des comptes bancaires (création, consultation, statistiques)
- Tester le service avec des outils comme grpcurl

## 🏗️ **Architecture**
- **Framework Backend** : Spring Boot 3.5.9
- **Communication** : gRPC (Google Remote Procedure Call)
- **Protocole** : Protocol Buffers (Protobuf)
- **Base de données** : ConcurrentHashMap (mémoire)
- **Ports** :
  - Application Spring Boot : 8080
  - Service gRPC : 9090

## 📁 **Structure du Projet**
```
demo/
├── src/main/java/com/example/demo/
│   ├── grpc/AccountGrpcService.java      # Implémentation du service gRPC
│   └── DemoApplication.java               # Classe principale Spring Boot
├── src/main/proto/
│   └── ComptesService.proto              # Définition du service Protobuf
├── src/main/resources/
│   └── application.yml                   # Configuration
├── pom.xml                               # Dépendances Maven
└── README.md                             # Documentation
```

## ⚙️ **Prérequis**
- Java 17+
- Maven 3.6+
- grpcurl (pour tester le service)
- PowerShell (sur Windows)

## 🚀 **Installation et Exécution**

### **1. Cloner le projet**
```bash
# Clonez le projet ou créez la structure manuellement
```

### **2. Installer les dépendances**
```bash
mvn clean compile
```

### **3. Démarrer l'application**
```bash
mvn spring-boot:run
```

### **4. Vérifier que le service est démarré**
Dans les logs, vous devriez voir :
```
NettyServer started on port(s): 9090
=== Service gRPC ComptesService démarré ===
```

## 🔧 **Configuration**

### **Fichier `application.yml`**
```yaml
spring:
  application:
    name: demo-grpc

grpc:
  server:
    port: 9090
    reflection-service-enabled: true

logging:
  level:
    com.example.demo: INFO
    net.devh.boot.grpc: INFO
```

### **Fichier `pom.xml` - Dépendances principales**
- `grpc-server-spring-boot-starter` : Intégration gRPC avec Spring Boot
- `grpc-netty-shaded` : Serveur Netty pour gRPC
- `grpc-protobuf` : Support Protobuf
- `protobuf-java` : Bibliothèque Protobuf
- `protobuf-maven-plugin` : Génération des classes Java à partir des fichiers `.proto`

## 📡 **API gRPC**

### **Définition du Service (`ComptesService.proto`)**
```protobuf
service ComptesService {
    rpc AllComptes(GetAllComptesRequest) returns (GetAllComptesResponse);
    rpc CompteById(GetCompteByIdRequest) returns (GetCompteByIdResponse);
    rpc TotalSolde(GetTotalSoldeRequest) returns (GetTotalSoldeResponse);
    rpc SaveCompte(SaveCompteRequest) returns (SaveCompteResponse);
}
```

### **Méthodes disponibles**

#### **1. `AllComptes`**
- **Description** : Récupère tous les comptes bancaires
- **Requête** : `GetAllComptesRequest` (vide)
- **Réponse** : `GetAllComptesResponse` (liste de comptes)

#### **2. `CompteById`**
- **Description** : Récupère un compte par son ID
- **Requête** : `GetCompteByIdRequest` (contient l'ID)
- **Réponse** : `GetCompteByIdResponse` (compte trouvé)

#### **3. `TotalSolde`**
- **Description** : Calcule les statistiques des soldes
- **Requête** : `GetTotalSoldeRequest` (vide)
- **Réponse** : `GetTotalSoldeResponse` (statistiques)

#### **4. `SaveCompte`**
- **Description** : Crée un nouveau compte
- **Requête** : `SaveCompteRequest` (données du compte)
- **Réponse** : `SaveCompteResponse` (compte créé)

## 🧪 **Tests du Service**

### **1. Installation de grpcurl**
```powershell
# Windows avec Scoop
scoop install grpcurl

# Vérification
grpcurl --version
```

### **2. Commandes de test**

#### **Lister les services disponibles**
```powershell
grpcurl -plaintext localhost:9090 list
```

#### **Créer un compte**
```powershell
grpcurl -plaintext -d '{
  "compte": {
    "solde": 1500.50,
    "dateCreation": "2024-01-20T10:30:00",
    "type": "COURANT"
  }
}' localhost:9090 com.example.demo.grpc.stubs.ComptesService/SaveCompte
```

#### **Récupérer tous les comptes**
```powershell
grpcurl -plaintext -d '{}' localhost:9090 com.example.demo.grpc.stubs.ComptesService/AllComptes
```

#### **Obtenir les statistiques**
```powershell
grpcurl -plaintext -d '{}' localhost:9090 com.example.demo.grpc.stubs.ComptesService/TotalSolde
```

#### **Récupérer un compte par ID**
```powershell
grpcurl -plaintext -d '{"id": "ID_DU_COMPTE"}' localhost:9090 com.example.demo.grpc.stubs.ComptesService/CompteById
```

### **3. Script de test PowerShell**
Créez `test-service.ps1` :
```powershell
Write-Host "=== Test du service gRPC ===" -ForegroundColor Cyan

# Créer un compte
$json = '{"compte": {"solde": 1500.50, "dateCreation": "2024-01-20T10:30:00", "type": "COURANT"}}'
grpcurl -plaintext -d $json localhost:9090 com.example.demo.grpc.stubs.ComptesService/SaveCompte

# Lister tous les comptes
grpcurl -plaintext -d '{}' localhost:9090 com.example.demo.grpc.stubs.ComptesService/AllComptes
```

## 💾 **Modèle de données**

### **Types de compte (Enum)**
```protobuf
enum TypeCompte {
    COURANT = 0;
    EPARGNE = 1;
}
```

### **Structure d'un compte**
```protobuf
message Compte {
    string id = 1;           // Identifiant unique
    float solde = 2;         // Solde du compte
    string dateCreation = 3; // Date de création
    TypeCompte type = 4;     // Type de compte
}
```

### **Statistiques**
```protobuf
message SoldeStats {
    int32 count = 1;     // Nombre total de comptes
    float sum = 2;       // Somme des soldes
    float average = 3;   // Moyenne des soldes
}
```

## 🔍 **Dépannage**

### **Problèmes courants et solutions**

#### **1. Erreur "Failed to connect to localhost port 9090"**
```bash
# Vérifiez que le service est démarré
Test-NetConnection -ComputerName localhost -Port 9090
```

#### **2. Erreur "Method not found"**
```bash
# Vérifiez le nom exact du service
grpcurl -plaintext localhost:9090 list
```

#### **3. Erreur de compilation Protobuf**
```bash
# Supprimez les fichiers .proto en double
rm src/main/proto/account.proto  # Gardez seulement ComptesService.proto

# Recompilez
mvn clean compile
```

#### **4. Problèmes de dépendances**
```bash
# Nettoyez et recompilez
mvn clean
mvn compile
```

## 📊 **Exemples de réponses**

### **Réponse de création de compte**
```json
{
  "compte": {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "solde": 1500.50,
    "dateCreation": "2024-01-20T10:30:00",
    "type": "COURANT"
  }
}
```

### **Réponse de statistiques**
```json
{
  "stats": {
    "count": 3,
    "sum": 7500.75,
    "average": 2500.25
  }
}
```

## 🧠 **Concepts techniques abordés**

### **gRPC (Google Remote Procedure Call)**
- Protocole RPC haute performance
- Utilise HTTP/2 et Protocol Buffers
- Supporte les appels unaires et streaming

### **Protocol Buffers (Protobuf)**
- Format de sérialisation binaire
- Plus efficace que JSON/XML
- Génération de code statiquement typé

### **Spring Boot Integration**
- Auto-configuration avec `grpc-server-spring-boot-starter`
- Annotation `@GrpcService` pour les implémentations
- Gestion du cycle de vie avec Spring

## 📈 **Améliorations possibles**

### **1. Persistance des données**
- Remplacer ConcurrentHashMap par une base de données (PostgreSQL, MySQL)
- Utiliser Spring Data JPA pour la persistance

### **2. Sécurité**
- Ajouter l'authentification gRPC (SSL/TLS)
- Implémenter l'autorisation avec JWT

### **3. Monitoring**
- Ajouter Spring Boot Actuator
- Intégrer Prometheus pour les métriques

### **4. Tests**
- Ajouter des tests unitaires
- Implémenter des tests d'intégration gRPC

### **5. Documentation**
- Générer la documentation avec protoc-gen-doc
- Ajouter des exemples d'appels client dans différents langages

## 👥 **Contribution**

1. Forkez le projet
2. Créez une branche pour votre fonctionnalité
3. Committez vos changements
4. Poussez vers la branche
5. Ouvrez une Pull Request

## 📝 **Démonstration**

<img width="955" height="539" alt="TP18 1 " src="https://github.com/user-attachments/assets/4090c9f8-faf3-4b91-bd44-cfc46ae0f080" />

<img width="959" height="493" alt="TP18 2" src="https://github.com/user-attachments/assets/3cb1cc9b-7b76-4c54-8c7d-685732e31a26" />


<img width="959" height="524" alt="TP18 3" src="https://github.com/user-attachments/assets/89eb3da6-cd34-4fbe-bac1-73a87d7aa415" />


## 📄 **Licence**
Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 🙏 **Remerciements**

Encadré par : Pr.Mohamed Lechgar

Réalisé par : ettouyjer yasmine.

**Date du TP** : Janvier 2024  
**Environnement** : Windows 11, Java 17, Spring Boot 3.5.9  
**Objectif atteint** : ✅ Service gRPC fonctionnel avec 4 méthodes opérationnelles

Pour toute question, contactez [votre email] ou ouvrez une issue sur le dépôt.
