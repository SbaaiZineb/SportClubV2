package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepo extends JpaRepository<Paiement,Long> {
Paiement findByMember(Member member);
List<Paiement> findPaiementByMember(Member member);
List<Paiement> findByMemberTeleContains(String kw);
Page<Paiement> findByMemberNameContains(String kw,Pageable pageable);
List<Paiement> getPaiementByMember(Member member);
List<Paiement> getPaiementByAbonnement(Abonnement abonnement);

}
