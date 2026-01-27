package lyhongdang.book.service;

import lombok.RequiredArgsConstructor;
import lyhongdang.book.common.PageResponse;
import lyhongdang.book.dto.request.BannerRequest;
import lyhongdang.book.dto.request.response.BannerResponse;
import lyhongdang.book.entity.Banner;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.BannerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BannerService {
    private final BannerRepository bannerRepository;
    private final FileSerVice fileSerVice;

    @PreAuthorize("hasAnyRole('ADMIN')")
    public BannerResponse createBanner(String folder, MultipartFile file, BannerRequest request) throws Exception {
        try {
            Map<?, ?> uploadResult = fileSerVice.uploadFile(file, folder);
            String imageUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            Banner banner = Banner.builder()
                    .imageUrl(imageUrl)
                    .publicId(publicId)
                    .name(request.getName())
                    .active(request.getActive())
                    .build();

            var result = bannerRepository.save(banner);
            return BannerResponse.builder()
                    .name(result.getName())
                    .imageURL(result.getImageUrl())
                    .active(result.isActive())
                    .build();

        } catch (IOException e) {
            throw new Exception("Failed to upload banner image", e);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteBanner(Integer bannerId) throws Exception {
        try {
            Banner banner = bannerRepository.findById(bannerId)
                    .orElseThrow(() -> new BusinessException(ErrorCodes.BANNER_NOT_FOUND));
            fileSerVice.deleteFile(banner.getPublicId());
            bannerRepository.delete(banner);
        } catch (IOException e) {
            throw new Exception("Failed to delete banner image", e);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public BannerResponse updateBanner(MultipartFile file, String folder, Integer bannerId) throws Exception {
        try {
            Banner oldBanner = bannerRepository.findById(bannerId)
                    .orElseThrow(() -> new BusinessException(ErrorCodes.BANNER_NOT_FOUND));

            fileSerVice.deleteFile(oldBanner.getPublicId());
            Map<?, ?> uploadResult = fileSerVice.uploadFile(file, folder);

            String imageUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            oldBanner.setImageUrl(imageUrl);
            oldBanner.setPublicId(publicId);
            var updatedBanner = bannerRepository.save(oldBanner);
            return BannerResponse.builder()
                    .name(updatedBanner.getName())
                    .imageURL(updatedBanner.getImageUrl())
                    .active(updatedBanner.isActive())
                    .build();
        } catch (IOException e) {
            throw new Exception("Failed to update banner image", e);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public BannerResponse getBannerById(Integer bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.BANNER_NOT_FOUND));
       return BannerResponse.builder()
                .name(banner.getName())
                .imageURL(banner.getImageUrl())
                .active(banner.isActive())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public PageResponse<BannerResponse> getAllBanner(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").ascending());
        Page<Banner> bannerPage = bannerRepository.findAll(pageable);
        List<BannerResponse> bannerResponses = new ArrayList<>();
        for (Banner banner: bannerPage.getContent()) {
            BannerResponse bookResponse = BannerResponse.builder()
                    .name(banner.getName())
                    .imageURL(banner.getImageUrl())
                    .active(banner.isActive())
                    .build();
            bannerResponses.add(bookResponse);
        }

        return PageResponse.<BannerResponse>builder()
                .content(bannerResponses)
                .number(bannerPage.getNumber())
                .size(bannerPage.getSize())
                .totalElements(bannerPage.getTotalElements())
                .totalPages(bannerPage.getTotalPages())
                .first(bannerPage.isFirst())
                .last(bannerPage.isLast())
                .build();
    }
}
