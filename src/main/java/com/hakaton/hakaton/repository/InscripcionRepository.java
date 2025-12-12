package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.InscripcionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<InscripcionModel, Long> {

    List<InscripcionModel> findByUsuarioIdUsuario(Long idUsuario);

    int countByUsuarioIdUsuario(Long idUsuario);

    Optional<InscripcionModel> findByUsuarioIdUsuarioAndHackatonIdHackaton(Long idUsuario, Long idHackaton);

    boolean existsByUsuarioIdUsuarioAndHackatonIdHackaton(Long idUsuario, Long idHackaton);

    List<InscripcionModel> findByHackatonIdHackaton(Long idHackaton);

    int countByHackatonIdHackaton(Long idHackaton);

    void deleteByUsuarioIdUsuarioAndHackatonIdHackaton(Long idUsuario, Long idHackaton);
}
