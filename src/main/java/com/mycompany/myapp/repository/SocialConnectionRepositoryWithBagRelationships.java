package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SocialConnection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface SocialConnectionRepositoryWithBagRelationships {
    Optional<SocialConnection> fetchBagRelationships(Optional<SocialConnection> socialConnection);

    List<SocialConnection> fetchBagRelationships(List<SocialConnection> socialConnections);

    Page<SocialConnection> fetchBagRelationships(Page<SocialConnection> socialConnections);
}
