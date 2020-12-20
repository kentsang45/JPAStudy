package jpa.jpaservice.domainPakage;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Member와의 연관관계에서 주인이다.
@Entity
@Table(name = "orders")
@Getter @Setter
public class Order
{
	@Id
	@GeneratedValue
	@Column(name = "order_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id") // 어떤 컬럼에 들어갈 것인가?
	private Member member;
	
	@OneToMany(mappedBy = "order" // orderItem에 있는 order 변수와 mapping 된다.
					  , cascade = CascadeType.ALL) // OrderItem을 persist 자동으로 하게 해준다...
	private List<OrderItem> orderItems = new ArrayList<>();
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // 일대일 대응관계일 때, 누가 주인? 다른 객체를 조회하는 객체가 주인
	@JoinColumn(name = "delivery_id")
	private Delivery delivery;
	
	private LocalDateTime orderDate;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus; // 주문 상태 (주문, 취소)
	
	
	//== 연관관계 편의 메서드 ==//
	public void setMember(Member member)
	{
		this.member = member;
		member.getOrders().add(this);
	}
	
	public void addOrderItem(OrderItem orderItem)
	{
		orderItems.add(orderItem);
		orderItem.setOrder(this);
	}
	
	public void setDelivery(Delivery delivery)
	{
		this.delivery = delivery;
		delivery.setOrder(this);
	}
}
