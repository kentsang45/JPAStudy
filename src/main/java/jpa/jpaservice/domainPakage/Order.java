package jpa.jpaservice.domainPakage;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Member와의 연관관계에서 주인이다.
@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
	
	//== 생성 메서드 ==//
	// 주문 생성
	public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems)
	{
		Order order = new Order();
		order.setMember(member);
		order.setDelivery(delivery);
		for(OrderItem orderItem : orderItems)
		{
			order.addOrderItem(orderItem);
		}
		
		order.setOrderStatus(OrderStatus.ORDER);
		order.setOrderDate(LocalDateTime.now());
		return order;
	}
	
	//== 비즈니스 로직 ==//
	// 캔슬 버튼 = 주문 취소
	public void cancel()
	{
		if(DeliveryStatus.COMPLETED == delivery.getDeliveryStatus())
		{
			throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
		}
		
		this.setOrderStatus(OrderStatus.CANCEL);
		
		for(OrderItem item : this.orderItems)
		{
			item.cancel();
		}
	}
	
	//== 조회 로직 ==//
	// 전체 주문가격 계산, 반환
	public int getTotalPrice()
	{
		int totalPrice = 0;
		
		for(OrderItem item : orderItems)
		{
			totalPrice += item.getTotalPrice();
		}
		
		return totalPrice;
	}
	
	//
	
}
