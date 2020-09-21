package com.example.dao;


import com.example.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface OrderRepository  extends JpaRepository<Order, Long> {
}