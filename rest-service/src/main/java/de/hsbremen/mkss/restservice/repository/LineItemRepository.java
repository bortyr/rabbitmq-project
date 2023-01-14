package de.hsbremen.mkss.restservice.repository;

import de.hsbremen.mkss.restservice.entity.LineItem;
import org.springframework.data.repository.CrudRepository;

public interface LineItemRepository extends CrudRepository<LineItem, Long> {
}
