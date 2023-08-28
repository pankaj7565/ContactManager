package com.smart.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.entities.MyOrder;

public interface MyOrderRepo extends JpaRepository<MyOrder,Long > {

	
	public MyOrder findByOrderId(String orderId);
}
