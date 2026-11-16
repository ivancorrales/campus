package es.academia.campushub.repository;

import es.academia.campushub.document.SeguimientoDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SeguimientoRepository extends MongoRepository<SeguimientoDocument, String> {

  List<SeguimientoDocument> findByActividadIdOrderByCreadoEnAsc(Long actividadId);
}
