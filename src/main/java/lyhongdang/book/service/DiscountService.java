package lyhongdang.book.service;

import lombok.RequiredArgsConstructor;
import lyhongdang.book.common.PageResponse;
import lyhongdang.book.dto.request.DiscountRequest;
import lyhongdang.book.dto.request.UpdateDiscountRequest;
import lyhongdang.book.dto.request.response.DiscountResponse;
import lyhongdang.book.entity.Discount;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.DiscountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;

    @PreAuthorize("hasAnyRole('ADMIN')")
    public DiscountResponse createDiscount(DiscountRequest request){
        Discount discount = new Discount();
        discount.setCode(request.getCode());
        discount.setPercentage(request.getPercentage());
        discount.setActive(false);
        var result = discountRepository.save(discount);
        DiscountResponse response = new DiscountResponse();
        response.setCode(result.getCode());
        response.setPercentage(result.getPercentage());
        response.setActive(result.isActive());
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public String activeDiscount(Integer discountId){
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.DISCOUNT_NOT_FOUND));
        discount.setActive(true);
        discountRepository.save(discount);
        return "Discount active successfully" + discountId;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public String deleteDiscount(Integer discountId){
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.DISCOUNT_NOT_FOUND));
        discountRepository.delete(discount);
        return "Discount deleted successfully" + discountId;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public DiscountResponse getDiscount(Integer discountId){
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.DISCOUNT_NOT_FOUND));
        DiscountResponse response = new DiscountResponse();
        response.setCode(discount.getCode());
        response.setPercentage(discount.getPercentage());
        response.setActive(discount.isActive());
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public PageResponse<DiscountResponse> getAll(int page, int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").ascending());
        Page<Discount> discountPage = discountRepository.findAll(pageable);
        List<DiscountResponse> responses = new ArrayList<>();
        for (Discount discount: discountPage.getContent()) {
            DiscountResponse response = new DiscountResponse();
            response.setCode(discount.getCode());
            response.setPercentage(discount.getPercentage());
            response.setActive(discount.isActive());
            responses.add(response);
        }
        return PageResponse.<DiscountResponse>builder()
                .content(responses)
                .number(discountPage.getNumber())
                .size(discountPage.getSize())
                .totalElements(discountPage.getTotalElements())
                .totalPages(discountPage.getTotalPages())
                .first(discountPage.isFirst())
                .last(discountPage.isLast())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public DiscountResponse updateDiscount(Integer discountId, UpdateDiscountRequest request){
        var discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.DISCOUNT_NOT_FOUND));
        discount.setCode(request.getCode());
        discount.setPercentage(request.getPercentage());
        discount.setActive(request.getActive());
        var result = discountRepository.save(discount);
        DiscountResponse response = new DiscountResponse();
        response.setCode(result.getCode());
        response.setPercentage(result.getPercentage());
        response.setActive(result.isActive());
        return response;
    }
}
