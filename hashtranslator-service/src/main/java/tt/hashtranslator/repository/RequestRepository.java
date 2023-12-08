package tt.hashtranslator.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tt.hashtranslator.entity.Request;

@Repository
public interface RequestRepository extends MongoRepository<Request, String> {
}
