package com.prgrms.catchtable.shop.repository;

import static com.prgrms.catchtable.shop.domain.QShop.*;

import com.prgrms.catchtable.shop.domain.Category;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.ShopSearchCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ShopRepositoryImpl implements ShopRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ShopRepositoryImpl(EntityManager em){
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Shop> findSearch(ShopSearchCondition condition) {
        return queryFactory
            .selectFrom(shop)
            .where(
                equalsName(condition.name()),
                equalCategory(condition.category()),
                equalCity(condition.city())
            )
            .orderBy(
                //평점 내림차순
                ratingDesc()
            )
            .fetch();
    }

    private BooleanExpression equalsName(String name){
        if(name.isEmpty()){
            return null;
        }

        return shop.name.eq(name);
    }

    private BooleanExpression equalCategory(String category){
        return shop.category.eq(Category.of(category));
    }

    private BooleanExpression equalCity(String city) {
        return shop.address.city.eq(city);
    }

    private OrderSpecifier<?> ratingDesc() {
        return shop.rating.desc();
    }
}
