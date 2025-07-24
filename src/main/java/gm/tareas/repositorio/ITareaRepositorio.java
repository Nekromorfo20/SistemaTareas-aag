package gm.tareas.repositorio;

import gm.tareas.modelo.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITareaRepositorio extends JpaRepository<Tarea, Integer> {
}
