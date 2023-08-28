package com.smart.entities;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="orders")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MyOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long myOrderId;
	
	private String orderId;
	
	private String amount;
	
	private String receipt;
	
	private String status;
	
	
//	one user can have many payment
	@ManyToOne
	private User user;
	
	private String paymentId;
	
	
	
	
	
	
	
	
	

}
