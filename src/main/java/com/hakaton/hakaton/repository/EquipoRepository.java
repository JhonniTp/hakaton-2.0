package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.EquipoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<EquipoModel, Long> {

        @Query("SELECT e FROM EquipoModel e WHERE e.hackaton.idHackaton = :idHackaton")
        List<EquipoModel> findByHackatonId(@Param("idHackaton") Long idHackaton);

        @Query("SELECT e FROM EquipoModel e WHERE e.nombreEquipo = :nombreEquipo AND e.hackaton.idHackaton = :idHackaton")
        Optional<EquipoModel> findByNombreEquipoAndHackatonId(
                        @Param("nombreEquipo") String nombreEquipo,
                        @Param("idHackaton") Long idHackaton);

        @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EquipoModel e WHERE e.nombreEquipo = :nombreEquipo AND e.hackaton.idHackaton = :idHackaton")
        boolean existsByNombreEquipoAndHackatonId(
                        @Param("nombreEquipo") String nombreEquipo,
                        @Param("idHackaton") Long idHackaton);

        @Query("SELECT COUNT(e) FROM EquipoModel e WHERE e.hackaton.idHackaton = :idHackaton")
        long countByHackatonId(@Param("idHackaton") Long idHackaton);

        // Alias method for service layer compatibility
        default List<EquipoModel> findByHackatonIdHackaton(Long idHackaton) {
                return findByHackatonId(idHackaton);
        }
}
