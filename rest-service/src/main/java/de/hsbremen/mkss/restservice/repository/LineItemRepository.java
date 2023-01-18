package de.hsbremen.mkss.restservice.repository;

import de.hsbremen.mkss.restservice.controllers.entity.LineItem;
import org.springframework.data.repository.CrudRepository;

public interface LineItemRepository extends CrudRepository<LineItem, Long> {
}
