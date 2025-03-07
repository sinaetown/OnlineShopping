package com.ecommerce.online_shopping.order;

import com.ecommerce.online_shopping.item.Item;
import com.ecommerce.online_shopping.item.ItemRepository;
import com.ecommerce.online_shopping.member.MemberRepository;
import com.ecommerce.online_shopping.member.domain.Member;
import com.ecommerce.online_shopping.order.domain.OrderStatus;
import com.ecommerce.online_shopping.order.domain.Ordering;
import com.ecommerce.online_shopping.order.dto.OrderReqDto;
import com.ecommerce.online_shopping.order.dto.OrderResDto;
import com.ecommerce.online_shopping.order_item.OrderItem;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository, MemberRepository memberRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.itemRepository = itemRepository;
    }

    public Ordering create(List<OrderReqDto> orderReqDtos) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("일치하는 이메일의 회원이 없어요."));
        Ordering ordering = Ordering.builder().member(member).build();
        for (OrderReqDto dto : orderReqDtos) {
            Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new EntityNotFoundException("일치하는 ID의 상품이 없어요."));
            OrderItem orderItem = OrderItem
                    .builder()
                    .quantity(dto.getCount())
                    .item(item)
                    .ordering(ordering)
                    .build();
            ordering.getOrderItems().add(orderItem);
            if (item.getStockQuantity() - dto.getCount() < 0) {
                throw new IllegalArgumentException("재고가 없어요.");
            }
            orderItem.getItem().updateStockQuantity(item.getStockQuantity() - dto.getCount());
        }
        return orderRepository.save(ordering);
    }

    public Ordering cancel(Long id) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Ordering ordering = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("일치하는 주문번호가 없어요."));
        if (ordering.getOrderStatus() == OrderStatus.CANCELED) {
            throw new IllegalArgumentException("이미 취소된 주문이에요.");
        }
        if (authentication.getAuthorities().contains((new SimpleGrantedAuthority("ROLE_ADMIN")))
                || ordering.getMember().getEmail().equals(email)) {
            ordering.cancelOrder();
            for (OrderItem o : ordering.getOrderItems()) {
                o.getItem().increaseStockQuantity(o.getItem().getStockQuantity() + o.getQuantity());
            }
        } else {
            throw new AccessDeniedException("본인이 주문한 주문내역만 삭제할 수 있어요.");
        }
        return ordering;
    }

    public List<OrderResDto> findAll() {
        List<Ordering> orderings = orderRepository.findAll();
        return orderings.stream().map(m -> OrderResDto.toDto(m)).collect(Collectors.toList());
    }

    public List<OrderResDto> findMyOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("일치하는 이메일의 회원이 없어요."));
        List<Ordering> orderings = orderRepository.findByMemberId(member.getId());
        return orderings.stream().map(m -> OrderResDto.toDto(m)).collect(Collectors.toList());

    }

    public List<OrderResDto> findByMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(()->new EntityNotFoundException("일치하는 ID의 회원이 없어요."));
        List<Ordering> orderings = member.getOrderings();
        return orderings.stream().map(m->OrderResDto.toDto(m)).collect(Collectors.toList());
    }

}
