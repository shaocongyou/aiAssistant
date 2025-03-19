package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SocialConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class SocialConnectionRepositoryWithBagRelationshipsImpl implements SocialConnectionRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String SOCIALCONNECTIONS_PARAMETER = "socialConnections";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<SocialConnection> fetchBagRelationships(Optional<SocialConnection> socialConnection) {
        return socialConnection.map(this::fetchUsers);
    }

    @Override
    public Page<SocialConnection> fetchBagRelationships(Page<SocialConnection> socialConnections) {
        return new PageImpl<>(
            fetchBagRelationships(socialConnections.getContent()),
            socialConnections.getPageable(),
            socialConnections.getTotalElements()
        );
    }

    @Override
    public List<SocialConnection> fetchBagRelationships(List<SocialConnection> socialConnections) {
        return Optional.of(socialConnections).map(this::fetchUsers).orElse(Collections.emptyList());
    }

    SocialConnection fetchUsers(SocialConnection result) {
        return entityManager
            .createQuery(
                "select socialConnection from SocialConnection socialConnection left join fetch socialConnection.users where socialConnection.id = :id",
                SocialConnection.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<SocialConnection> fetchUsers(List<SocialConnection> socialConnections) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, socialConnections.size()).forEach(index -> order.put(socialConnections.get(index).getId(), index));
        List<SocialConnection> result = entityManager
            .createQuery(
                "select socialConnection from SocialConnection socialConnection left join fetch socialConnection.users where socialConnection in :socialConnections",
                SocialConnection.class
            )
            .setParameter(SOCIALCONNECTIONS_PARAMETER, socialConnections)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
