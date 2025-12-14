package com.nhn.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorCdMsg {
    public static final String FUNC_UNAUTHORIZED_ERR_MSG = "Vui lòng đăng nhập để tiếp tục!";
    public static final String FUNC_FORBIDDEN_ERR_MSG = "Bạn không có quyền để thực hiện hành động này. Vui lòng liên hệ quản trị viên để biết thêm chi tiết!";

    public static final String CLOUDINARY_UPLOAD_ERR_MSG = "Xảy ra lỗi khi upload hình ảnh lên Cloudinary. Vui lòng thử lại!";
    public static final String CLOUDINARY_DELETE_ERR_MSG = "Xảy ra lỗi khi xóa hình ảnh trên Cloudinary. Vui lòng thử lại!";

    public static final String VALIDATION_IMAGE_EXTENSION_ERR_MSG = "Định dạng hình ảnh [{0}] không hợp lệ. Vui lòng kiểm tra lại!";
    public static final String VALIDATION_IMAGE_SIZE_ERR_MSG      = "Kích thước hình ảnh không được vượt quá {0}MB. Vui lòng kiểm tra lại!";

    public static final String FUNC_EMAIL_EXISTS_ERR_MSG                   = "Email [{0}] đã tồn tại trong hệ thống. Vui lòng kiểm tra lại!";
    public static final String FUNC_EMAIL_NOT_EXISTS_ERR_MSG               = "Email [{0}] không tồn tại trong hệ thống. Vui lòng kiểm tra lại!";
    public static final String FUNC_LOGIN_FAILED_ERR_MSG                   = "Đã xảy ra lỗi hệ thống trong quá trình đăng nhập. Vui lòng thử lại!";
    public static final String FUNC_LOGIN_EMAIL_PASSWORD_NOT_MATCH_ERR_MSG = "Email hoặc mật khẩu không chính xác. Vui lòng kiểm tra lại!";
    public static final String FUNC_LOGIN_USER_INACTIVE_ERR_MSG            = "Tài khoản của bạn chưa được xác minh. Vui lòng thực hiện xác minh email để tiếp tục!";
    public static final String FUNC_VERIFY_EMAIL_ALREADY_ERR_MSG           = "Email của bạn đã được xác minh trước đó. Vui lòng đăng nhập để tiếp tục!";
    public static final String FUNC_VERIFY_EMAIL_INVALID_CODE_ERR_MSG      = "Mã xác minh không chính xác. Vui lòng kiểm tra lại!";

    public static final String FUNC_PERMISSION_EXISTS_ERR_MSG = "Quyền [{0}] đã tồn tại trong hệ thống. Vui lòng kiểm tra lại!";

    public static final String FUNC_BOOK_CATEGORY_NOT_FOUND_ERR_MSG     = "Danh mục sách không tồn tại trong hệ thống. Vui lòng kiểm tra lại!";
    public static final String FUNC_BOOK_CATEGORY_SLUG_EXISTS_ERR_MSG   = "Danh mục sách [{0}] đã tồn tại trong hệ thống. Vui lòng kiểm tra lại!";
    public static final String FUNC_BOOK_CATEGORY_INVALID_DEPTH_ERR_MSG = "Hệ thống chỉ hỗ trợ tối đa {0} cấp danh mục sách. Vui lòng kiểm tra lại!";

    public static final String FUNC_BOOK_PUBLISHER_NOT_FOUND_ERR_MSG   = "NXB không tồn tại trong hệ thống. Vui lòng kiểm tra lại!";
    public static final String FUNC_BOOK_PUBLISHER_SLUG_EXISTS_ERR_MSG = "NXB [{0}] đã tồn tại trong hệ thống. Vui lòng kiểm tra lại!";

    public static final String FUNC_BOOK_AUTHOR_NOT_FOUND_ERR_MSG   = "Tác giả không tồn tại trong hệ thống. Vui lòng kiểm tra lại!";
    public static final String FUNC_BOOK_AUTHOR_SLUG_EXISTS_ERR_MSG = "Tác giả [{0}] đã tồn tại trong hệ thống. Vui lòng kiểm tra lại!";

    public static final String FUNC_BOOK_NOT_FOUND_ERR_MSG   = "Sách không tồn tại trong hệ thống. Vui lòng kiểm tra lại!";
    public static final String FUNC_BOOK_SLUG_EXISTS_ERR_MSG = "Sách [{0}] đã tồn tại trong hệ thống. Vui lòng kiểm tra lại!";


    public static final String FUNC_ROLE_NO_EXISTS_ERR_MSG                 = "Role [{0}] đã tồn tại trong hệ thống. Vui lòng kiểm tra lại!";
    public static final String FUNC_ROLE_NO_NOT_EXISTS_ERR_MSG             = "Role [{0}] không tồn tại trong hệ thống. Vui lòng kiểm tra lại!";
    public static final String FUNC_ROLE_NOT_FOUND_ERR_MSG                 = "Role không tồn tại trong hệ thống. Vui lòng kiểm tra lại!";
    public static final String FUNC_ROLE_UPDATE_PERMISSION_INVALID_REQUEST = "Danh sách quyền cập nhật không hợp lệ. Vui lòng kiểm tra lại!";
}
