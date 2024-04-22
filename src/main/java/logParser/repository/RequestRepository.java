package logParser.repository;

import logParser.dataModel.RequestEntity;
import org.springframework.data.repository.CrudRepository;

public interface RequestRepository extends CrudRepository<RequestEntity, Long> {
}
