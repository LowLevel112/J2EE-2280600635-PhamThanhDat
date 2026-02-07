package com.example._PhamThanhDat.repositories;

import com.example._PhamThanhDat.entities.ItemInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IItemInvoiceRepository extends
                JpaRepository<ItemInvoice, Long> {
}
