package de.hsbremen.mkss.restservice.exceptions.repository;

import java.util.List;

import de.hsbremen.mkss.restservice.entity.oorder;
import org.springframework.data.repository.CrudRepository;

public interface oorderRepository extends CrudRepository<oorder,Long> {
}
