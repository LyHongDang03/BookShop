package lyhongdang.book.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodes {
    // COMMON
    NO_CODE(1000, "No code", HttpStatus.NO_CONTENT),
    INTERNAL_ERROR(1001, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(1002, "Unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1003, "Forbidden", HttpStatus.FORBIDDEN),
    NOT_FOUND(1004, "Resource not found", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR(1005, "Validation failed", HttpStatus.BAD_REQUEST),

    // USER / AUTH
    ACCOUNT_LOCKED(2000, "User account locked", HttpStatus.LOCKED), // 423 LOCKED
    ACCOUNT_DISABLED(2001, "User account is disabled", HttpStatus.FORBIDDEN),
    INCORRECT_CURRENT_PASSWORD(2002, "Current password is incorrect", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_DOES_NOT_MATCH(2003, "The new password does not match", HttpStatus.BAD_REQUEST),
    BAD_CREDENTIALS(2004, "Login and/or password is incorrect", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(2005, "Token expired or invalid", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(2006, "User not found", HttpStatus.NOT_FOUND),
    TOKEN_NOT_FOUND(2007, "Token not found", HttpStatus.NOT_FOUND),

    // CART
    CART_EMPTY(4000, "Cart is empty", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(4001, "Cart item not found", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_SELECTED(4002, "No items selected for checkout", HttpStatus.BAD_REQUEST),

    // BOOK / PRODUCT / CATEGORY / IMAGE
    BOOK_NOT_FOUND(4100, "Book not found", HttpStatus.NOT_FOUND),
    BOOK_OUT_OF_STOCK(4101, "Not enough stock for the book", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(4102, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_ALLOWED(4103, "CategoryIds must not be null or empty", HttpStatus.BAD_REQUEST),
    DISCOUNT_NOT_FOUND(4104, "Discount not found", HttpStatus.NOT_FOUND),
    IMAGE_NOT_FOUND(4105, "Image not found", HttpStatus.NOT_FOUND),
    IMAGE_NOT_ALLOWED(4106, "File number and image ID list size must match", HttpStatus.BAD_REQUEST),
    BANNER_NOT_FOUND(4107, "Banner not found", HttpStatus.NOT_FOUND),

    // ORDER
    ORDER_NOT_FOUND(4200, "Order not found", HttpStatus.NOT_FOUND),
    ORDER_DETAIL_NOT_FOUND(4201, "Order detail not found", HttpStatus.NOT_FOUND),
    ORDER_UNAUTHORIZED(4202, "You are not authorized to access this order", HttpStatus.FORBIDDEN),

    // EMAIL / PERMISSION / ROLE (4300–4399)
    EMAIL_SENDING_FAILED(4300, "Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR),
    PERMISSION_NOT_FOUND(4301, "Permission not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(4302, "Role not found", HttpStatus.NOT_FOUND),

    // REPORT
    REPORT_GENERATION_FAILED(4400, "Unable to generate report", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCodes(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
