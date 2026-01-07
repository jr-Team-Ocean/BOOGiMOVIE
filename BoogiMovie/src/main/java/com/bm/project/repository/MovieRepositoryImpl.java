package com.bm.project.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bm.project.entity.Category;
import com.bm.project.entity.Likes;
import com.bm.project.entity.Member;
import com.bm.project.entity.Movie;
import com.bm.project.entity.Product;
import com.bm.project.entity.ProductTag;
import com.bm.project.entity.ProductTagConnect;
import com.bm.project.entity.ProductType;
import com.bm.project.entity.Review;
import com.bm.project.entity.TagCode;
import com.bm.project.enums.CommonEnums;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class MovieRepositoryImpl implements MovieRepositoryCustom{
	
	@PersistenceContext
	private EntityManager em;

	// 영화 목록 조회
	@Override
	public Page<Movie> selectMovieList(Map<String, Object> paramMap, Pageable pageable) {
		return runListQuery(paramMap, pageable, false);
	}

	// 영화 검색 조회
	@Override
	public Page<Movie> searchMovieList(Map<String, Object> paramMap, Pageable pageable) {
		return runListQuery(paramMap, pageable, true);
	}
	
	private Page<Movie> runListQuery(Map<String, Object> paramMap, Pageable pageable, boolean isSearch){
		
		// 카테고리(단일) 
		Long categoryId = null;
		if (paramMap.get("category") != null && !paramMap.get("category").toString().isBlank()) {
            categoryId = Long.valueOf(paramMap.get("category").toString());
        }
		
		// 카테고리(복수: 전체 탭에서 장르를 국내+해외 둘 다 검색)
		List<Long> categoryIds = null;
        if (paramMap.get("categoryIds") != null && !paramMap.get("categoryIds").toString().isBlank()) {
            String[] arr = paramMap.get("categoryIds").toString().split(",");
            categoryIds = Arrays.stream(arr)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::valueOf)
                    .toList();
        }
        
        boolean hasCategory = (categoryId != null && categoryId != 0);
        boolean hasCategoryIds = (categoryIds != null && !categoryIds.isEmpty());

        // categoryIds만 들어오는 경우도 카테고리 필터를 적용해야 함
        boolean hasAnyCategoryFilter = hasCategory || hasCategoryIds;

        // 정렬
        String sort = String.valueOf(paramMap.getOrDefault("sort", "latest"));
        boolean popular = "popular".equals(sort);

        // 검색 키워드
        String keyword = String.valueOf(paramMap.getOrDefault("query", "")).trim();
        boolean hasKeyword = !keyword.isBlank();
        

        // 목록 쿼리
        StringBuilder sb = new StringBuilder();
        sb.append("select m from Movie m ")
          .append(" join fetch m.product p ")
          .append(" where p.productDelFl = :delFl ")
          .append(" and p.productType.typeCode = :typeCode ");
        
       // System.out.println("LIST JPQL = " + sb);


        // 검색 조건 (제목 OR 감독/배우 태그명)
        if (isSearch && hasKeyword) {
            sb.append(" and (")
              .append(" lower(p.productTitle) like :kw ")
              .append(" or exists (")
              .append("     select 1 ")
              .append("     from ProductTagConnect ptc ")
              .append("     join ptc.productTag pt ")
              .append("     where ptc.product.productNo = p.productNo ")
              .append("       and pt.tagCode.tagCode in (2, 5) ") // 감독(2), 배우(5)
              .append("       and lower(pt.tagName) like :kw ")
              .append(" )")
              .append(" ) ");
        }
        
        // 카테고리 조건
    	// 전체 탭에서 "장르"를 누른 경우: 국내/해외 장르 2개를 함께 검색
    	if (hasCategoryIds) {
            sb.append(" and p.category.categoryId in :categoryIds ");
        }

    	else if (hasCategory) {
    		// 국내/해외 탭 자체(부모 100/200)를 누른 경우
    		if (categoryId == 100L || categoryId == 200L) {
    	        sb.append(" and (p.category.categoryId = :categoryId ")
    	          .append(" or p.category.pCategoryId.categoryId = :categoryId) ");
    	    
    		} else {
    	        sb.append(" and p.category.categoryId = :categoryId ");
    	    }
    	}
    	
        // 정렬
        if ("popular".equals(sort)) {
            sb.append(" order by (select count(l) from Likes l where l.product = p) desc, p.productDate desc ");
        
        } else {
            sb.append(" order by p.productDate desc ");
        }
        
        TypedQuery<Movie> q = em.createQuery(sb.toString(), Movie.class)
                .setParameter("delFl", CommonEnums.ProductDelFl.N)
                .setParameter("typeCode", 2);

        if (isSearch && hasKeyword) {
            q.setParameter("kw", "%" + keyword.toLowerCase() + "%");
        }
        
        if (hasCategoryIds) {
            q.setParameter("categoryIds", categoryIds);
            
        } else if (hasCategory) {
            q.setParameter("categoryId", categoryId);
        }
        
        List<Movie> movies = q
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        
        
        // count 쿼리
        StringBuilder csb = new StringBuilder();
        csb.append("select count(m) from Movie m ")
           .append(" join m.product p ")
           .append(" where p.productDelFl = :delFl ")
           .append(" and p.productType.typeCode = :typeCode ");

        if (isSearch && hasKeyword) {
            csb.append(" and (")
               .append(" lower(p.productTitle) like :kw ")
               .append(" or exists (")
               .append("     select 1 ")
               .append("     from ProductTagConnect ptc ")
               .append("     join ptc.productTag pt ")
               .append("     where ptc.product.productNo = p.productNo ")
               .append("       and pt.tagCode.tagCode in (2, 5) ")
               .append("       and lower(pt.tagName) like :kw ")
               .append(" )")
               .append(" ) ");
        }
        
        if (hasCategoryIds) {
        	csb.append(" and p.category.categoryId in :categoryIds ");
        }

    	else if (hasCategory) {
    		// 국내/해외 탭 자체(부모 100/200)를 누른 경우
    		if (categoryId == 100L || categoryId == 200L) {
    			csb.append(" and (p.category.categoryId = :categoryId ")
    	          .append(" or p.category.pCategoryId.categoryId = :categoryId) ");
    	    
    		} else {
    			csb.append(" and p.category.categoryId = :categoryId ");
    	    }
    	}

        TypedQuery<Long> cq = em.createQuery(csb.toString(), Long.class)
                .setParameter("delFl", CommonEnums.ProductDelFl.N)
                .setParameter("typeCode", 2);

        if (isSearch && hasKeyword) {
            cq.setParameter("kw", "%" + keyword.toLowerCase() + "%");
        }
        
        if (hasCategoryIds) {
            cq.setParameter("categoryIds", categoryIds);
            
        } else if (hasCategory) {
            cq.setParameter("categoryId", categoryId);
        }

        Long total = cq.getSingleResult();
        
        // System.out.println("category=" + paramMap.get("category"));
        // System.out.println("categoryIds=" + paramMap.get("categoryIds"));
        // System.out.println("result size=" + movies.size() + ", total=" + total);

        
		return new PageImpl<>(movies, pageable, total);
	}

	// 연결이 없으면 생성 + 저장
	@Override
	public void saveProductTagConnect(Product product, ProductTag tag) {

		Long productNo = product.getProductNo();
        Long tagNo = tag.getTagNo();

        if (productNo == null) {
            throw new IllegalStateException("Product가 아직 저장되지 않아 productNo가 없습니다.");
        }
        if (tagNo == null) {
            throw new IllegalStateException("ProductTag가 아직 저장되지 않아 tagNo가 없습니다.");
        }
        
        // 중복이면 스킵
        if (existsProductTagConnect(productNo, tagNo)) return;

        // 연결 엔티티 생성
        ProductTagConnect connect = ProductTagConnect.builder()
                .product(product)
                .productTag(tag)
                .build();
        
        connect.addProduct(product, tag);
        em.persist(connect);
	}

	// TagCode 검증
	@Override
	public TagCode getTagCodeRef(long tagCode) {
		return em.getReference(TagCode.class, tagCode);
	}

	//  PRODUCT_TAG_CONNECT에 (productNo, tagNo) 연결이 이미 있는지 확인
	@Override
	public boolean existsProductTagConnect(Long productNo, Long tagNo) {
		Long cnt = em.createQuery(
                "select count(ptc) " +
                "from ProductTagConnect ptc " +
                "where ptc.product.productNo = :productNo " +
                "and ptc.productTag.tagNo = :tagNo",
                Long.class
        )
        .setParameter("productNo", productNo)
        .setParameter("tagNo", tagNo)
        .getSingleResult();

        return cnt != null && cnt > 0;
	}

	// ======================================================================================================
	
	// 좋아요 insert
	@Override
	public int insertLike(Long productNo, Long memberNo) {
		
		Member memberRef = em.getReference(Member.class, memberNo);
		Product productRef = em.getReference(Product.class, productNo);
		
		Likes likes = Likes.builder()
						.member(memberRef)
						.product(productRef)
						.build();
		
		em.persist(likes); // 단순 insert
		em.flush(); // 즉시 반영
		return 1;
	}

	// ======================================================================================================
	
	// 후기 insert
	@Override
	public int insertReview(Long productNo, Long memberNo, Integer reviewScore, String reviewContent) {
		
		Review review = Review.builder()
							.productNo(productNo)
							.memberNo(memberNo)
							.reviewScore(reviewScore)
							.reviewContent(reviewContent)
							.build();
		em.persist(review);
		return 1;
	}

	
	// 후기 List
	@Override
	public List<Review> selectReviewList(Long productNo) {
		
		StringBuilder query = new StringBuilder();
		query.append("select r from Review r")
			.append(" join fetch r.member")
			.append(" where r.productNo =:productNo")
			.append(" order by r.reviewTime desc");
		
		
		return em.createQuery(query.toString(), Review.class)
				.setParameter("productNo", productNo)
				.getResultList();
	}

	// 평점
	@Override
	public Double selectReviewAvg(Long productNo) {
		
		String query = "select avg(r.reviewScore) " + 
					   "from Review r " +
					   "where r.productNo = :productNo";
		
		return em.createQuery(query, Double.class)
				 .setParameter("productNo", productNo)
				 .getSingleResult();
		
	}
	
	
	
	
	
}
