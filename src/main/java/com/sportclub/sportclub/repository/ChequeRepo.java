package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Cheque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChequeRepo extends JpaRepository<Cheque, Long> {

}
