package logParser.repository;

import logParser.dataModel.RequestModel;
import org.springframework.data.repository.CrudRepository;

public interface RequestRepository extends CrudRepository<RequestModel, Long> {
}
