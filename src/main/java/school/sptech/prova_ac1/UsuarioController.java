package school.sptech.prova_ac1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;


    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(usuarios);
    }


    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())
                && usuarioRepository.existsByCpf(usuario.getCpf())) {
            return ResponseEntity.status(409).body("Email e CPF já cadastrados");
        }
        else if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.status(409).body("Email já cadastrado");
        }
        else if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            return ResponseEntity.status(409).body("CPF já cadastrado");
        }

        try {
            Usuario salvo = usuarioRepository.save(usuario);
            return ResponseEntity.status(201).body(salvo);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Violação de integridade");
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


        @GetMapping("/filtro-data")
        public ResponseEntity<List<Usuario>> buscarPorDataNascimento(@RequestParam("nascimento") LocalDate nascimento) {
            List<Usuario> usuarios = usuarioRepository.findByDataNascimento(nascimento);
            return usuarios.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(usuarios);
        }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Integer id,
                                       @Valid @RequestBody Usuario usuario) {
        return usuarioRepository.findById(id).map(existente -> {
            if (!existente.getEmail().equals(usuario.getEmail())
                    && usuarioRepository.existsByEmail(usuario.getEmail())) {
                return ResponseEntity.status(409).body("Email já cadastrado");
            }
            if (!existente.getCpf().equals(usuario.getCpf())
                    && usuarioRepository.existsByCpf(usuario.getCpf())) {
                return ResponseEntity.status(409).body("CPF já cadastrado");
            }

            existente.setNome(usuario.getNome());
            existente.setEmail(usuario.getEmail());
            existente.setSenha(usuario.getSenha());
            existente.setCpf(usuario.getCpf());
            existente.setDataNascimento(usuario.getDataNascimento());
            Usuario atualizado = usuarioRepository.save(existente);
            return ResponseEntity.ok(atualizado);

        }).orElse(ResponseEntity.notFound().build());
    }
}
