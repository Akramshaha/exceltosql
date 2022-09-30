package co.in.nextgencoder.exceltosql.nativequery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Transactional
@Repository
public class ManualNativeQuery {

    @PersistenceContext
    private EntityManager entityManager;

    public String nativeCreateTableQuery(String nativeQuery){
        Query query = entityManager.createNativeQuery(nativeQuery);
        query.executeUpdate();
        return "SUCCESS";
    }

    public String nativeCreateInsertIntoQuery(String nativeQuery){
        Query query = entityManager.createNativeQuery(nativeQuery);
        query.executeUpdate();
        return "SUCCESS";
    }
}
