package school.sptech.prova_ac1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    List<Usuario> findByDataNascimento(LocalDate dataNascimento);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Usuario findByEmail(String email);

    Usuario findByCpf(String cpf);
}
