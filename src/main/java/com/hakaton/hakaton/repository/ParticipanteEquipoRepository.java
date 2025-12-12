package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.ParticipanteEquipoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipanteEquipoRepository extends JpaRepository<ParticipanteEquipoModel, Long> {

        @Query("SELECT pe FROM ParticipanteEquipoModel pe WHERE pe.equipo.idEquipo = :idEquipo")
        List<ParticipanteEquipoModel> findByEquipoId(@Param("idEquipo") Long idEquipo);

        @Query("SELECT pe FROM ParticipanteEquipoModel pe WHERE pe.usuario.idUsuario = :idUsuario")
        List<ParticipanteEquipoModel> findByUsuarioId(@Param("idUsuario") Long idUsuario);

        @Query("SELECT CASE WHEN COUNT(pe) > 0 THEN true ELSE false END FROM ParticipanteEquipoModel pe WHERE pe.usuario.idUsuario = :idUsuario AND pe.equipo.idEquipo = :idEquipo")
        boolean existsByUsuarioIdAndEquipoId(
                        @Param("idUsuario") Long idUsuario,
                        @Param("idEquipo") Long idEquipo);

        @Query("SELECT pe FROM ParticipanteEquipoModel pe WHERE pe.equipo.idEquipo = :idEquipo AND pe.esLider = true")
        Optional<ParticipanteEquipoModel> findLiderByEquipoId(@Param("idEquipo") Long idEquipo);

        @Query("SELECT COUNT(pe) FROM ParticipanteEquipoModel pe WHERE pe.equipo.idEquipo = :idEquipo")
        long countByEquipoId(@Param("idEquipo") Long idEquipo);

        List<ParticipanteEquipoModel> findByEquipoIdEquipo(Long idEquipo);

        @Query("SELECT CASE WHEN COUNT(pe) > 0 THEN true ELSE false END FROM ParticipanteEquipoModel pe WHERE pe.usuario.idUsuario = :idUsuario AND pe.equipo.hackaton.idHackaton = :idHackaton")
        boolean existsByUsuarioIdAndHackatonId(
                        @Param("idUsuario") Long idUsuario,
                        @Param("idHackaton") Long idHackaton);

        // Alias method for service layer compatibility
        default List<ParticipanteEquipoModel> findByUsuarioIdUsuario(Long idUsuario) {
                return findByUsuarioId(idUsuario);
        }
}
