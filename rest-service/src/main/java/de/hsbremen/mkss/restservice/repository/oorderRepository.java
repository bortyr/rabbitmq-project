package de.hsbremen.mkss.restservice.repository;

import de.hsbremen.mkss.restservice.controllers.entity.Oorder;
import org.springframework.data.repository.CrudRepository;

public interface oorderRepository extends CrudRepository<Oorder,Long> {
}
