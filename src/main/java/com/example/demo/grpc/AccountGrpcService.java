package com.example.demo.grpc;

import io.grpc.stub.StreamObserver;
import com.example.demo.grpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
@Slf4j
public class AccountGrpcService extends ComptesServiceGrpc.ComptesServiceImplBase {

    private final Map<String, Compte> compteDB = new ConcurrentHashMap<>();

    public AccountGrpcService() {
        log.info("=== Service gRPC ComptesService démarré ===");
        // Ajouter des données de test
        compteDB.put("test-1", Compte.newBuilder()
                .setId("test-1")
                .setSolde(1000.50f)
                .setDateCreation("2024-01-15T10:30:00")
                .setType(TypeCompte.COURANT)
                .build());

        compteDB.put("test-2", Compte.newBuilder()
                .setId("test-2")
                .setSolde(5000.75f)
                .setDateCreation("2024-01-16T14:45:00")
                .setType(TypeCompte.EPARGNE)
                .build());

        log.info("{} comptes de test ajoutés", compteDB.size());
    }

    @Override
    public void allComptes(GetAllComptesRequest request,
                           StreamObserver<GetAllComptesResponse> responseObserver) {
        log.info(">>> allComptes appelée");
        try {
            GetAllComptesResponse.Builder responseBuilder = GetAllComptesResponse.newBuilder();
            responseBuilder.addAllComptes(compteDB.values());
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            log.info("<<< allComptes: {} comptes retournés", compteDB.size());
        } catch (Exception e) {
            log.error("Erreur dans allComptes", e);
            responseObserver.onError(new Throwable("Erreur lors de la récupération des comptes"));
        }
    }

    @Override
    public void compteById(GetCompteByIdRequest request,
                           StreamObserver<GetCompteByIdResponse> responseObserver) {
        log.info(">>> compteById appelée avec id: {}", request.getId());
        try {
            Compte compte = compteDB.get(request.getId());
            if (compte != null) {
                responseObserver.onNext(GetCompteByIdResponse.newBuilder()
                        .setCompte(compte).build());
                responseObserver.onCompleted();
                log.info("<<< compteById: compte trouvé");
            } else {
                responseObserver.onError(new Throwable("Compte non trouvé avec l'ID: " + request.getId()));
                log.warn("<<< compteById: compte non trouvé");
            }
        } catch (Exception e) {
            log.error("Erreur dans compteById", e);
            responseObserver.onError(new Throwable("Erreur interne"));
        }
    }

    @Override
    public void totalSolde(GetTotalSoldeRequest request,
                           StreamObserver<GetTotalSoldeResponse> responseObserver) {
        log.info(">>> totalSolde appelée");
        try {
            int count = compteDB.size();
            float sum = 0;
            for (Compte compte : compteDB.values()) {
                sum += compte.getSolde();
            }
            float average = count > 0 ? sum / count : 0;

            SoldeStats stats = SoldeStats.newBuilder()
                    .setCount(count)
                    .setSum(sum)
                    .setAverage(average)
                    .build();

            responseObserver.onNext(GetTotalSoldeResponse.newBuilder().setStats(stats).build());
            responseObserver.onCompleted();
            log.info("<<< totalSolde: stats calculées");
        } catch (Exception e) {
            log.error("Erreur dans totalSolde", e);
            responseObserver.onError(new Throwable("Erreur lors du calcul des statistiques"));
        }
    }

    @Override
    public void saveCompte(SaveCompteRequest request,
                           StreamObserver<SaveCompteResponse> responseObserver) {
        log.info(">>> saveCompte appelée");
        try {
            CompteRequest compteReq = request.getCompte();
            String id = UUID.randomUUID().toString();

            Compte compte = Compte.newBuilder()
                    .setId(id)
                    .setSolde(compteReq.getSolde())
                    .setDateCreation(compteReq.getDateCreation())
                    .setType(compteReq.getType())
                    .build();

            compteDB.put(id, compte);

            responseObserver.onNext(SaveCompteResponse.newBuilder().setCompte(compte).build());
            responseObserver.onCompleted();
            log.info("<<< saveCompte: compte créé avec ID: {}", id);
        } catch (Exception e) {
            log.error("Erreur dans saveCompte", e);
            responseObserver.onError(new Throwable("Erreur lors de la sauvegarde du compte"));
        }
    }
}