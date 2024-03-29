package com.prgrms.catchtable.shop.repository;

import static com.prgrms.catchtable.shop.domain.QShop.shop;

import com.prgrms.catchtable.shop.domain.Category;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.ShopSearchCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ShopRepositoryImpl implements ShopRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ShopRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Shop> findSearch(ShopSearchCondition condition) {
        return queryFactory
            .selectFrom(shop)
            .where(
                containsName(condition.name()),
                equalCategory(condition.category()),
                equalCity(condition.city())
            )
            .orderBy(
                //평점 내림차순
                ratingDesc()
            )
            .fetch();
    }

    private BooleanExpression containsName(String name) {
        if (name == null) {
            return null;
        }

        return shop.name.contains(name);
    }

    private BooleanExpression equalCategory(String category) {
        if (category == null) {
            return null;
        }
        return shop.category.eq(Category.of(category));
    }

    private BooleanExpression equalCity(String city) {
        if (city == null) {
            return null;
        }
        return shop.address.city.eq(city);
    }

    private OrderSpecifier<?> ratingDesc() {
        return shop.rating.desc();
    }
}
