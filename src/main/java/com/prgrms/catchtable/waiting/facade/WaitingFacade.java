package com.prgrms.catchtable.waiting.facade;

import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_MEMBER;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_SHOP;

import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WaitingFacade {
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;

    public Member getMemberEntity(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(
            ()-> new NotFoundCustomException(NOT_EXIST_MEMBER)
        );
    }

    public Shop getShopEntity(Long shopId){
        return shopRepository.findById(shopId).orElseThrow(
            () -> new NotFoundCustomException(NOT_EXIST_SHOP)
        );
    }
}
