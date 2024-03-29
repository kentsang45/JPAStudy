package jpa.jpaservice.domainPakage;

import jpa.jpaservice.itemPackage.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

// 주문 상품
// 주문 객체와 연관관계. 관계의 주인
@Entity
@Getter
@Setter
public class OrderItem
{
	@Id
	@GeneratedValue
	@Column(name = "order_item_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;
	
	private int orderPrice; // 주문 가격
	
	private int count; // 주문 수량
	
	// 생성자 막기... createOrderItem을 통해서만 생성되어야한다.
	protected OrderItem()
	{
	
	}
	
	//== 생성 메서드 ==//
	public static OrderItem createOrderItem(Item item, int orderPrice, int count)
	{
		OrderItem orderItem = new OrderItem();
		orderItem.setItem(item);
		orderItem.setOrderPrice(orderPrice);
		orderItem.setCount(count);
		
		// 재고를 내린다.
		item.removeStock(count);
		
		return orderItem;
	}
	
	//== 비즈니스 로직 ==//
	public void cancel()
	{
		// 재고 수량을 원상복구(원복)
		getItem().addStock(count);
	}
	
	// 조회
	public int getTotalPrice()
	{
		return getOrderPrice() * getCount();
	}
	

	

	
}
