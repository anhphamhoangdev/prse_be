package com.hcmute.prse_be.constants;

public class ErrorMsg {


    public static final String SOMETHING_WENT_WRONG = "Đã có lỗi xảy ra ! ";
    public static final String FAILED_REGISTER = "Đăng ký tài khoản không thành công !";

    public static final String INVALID_SUB_CATEGORY = "Thể loại không tồn tại !";


    // STUDENT ERROR
    public static final String STUDENT_USERNAME_NOT_EXIST = "Tài khoản người dùng không tồn tại !";
    public static final String STUDENT_EMAIL_NOT_EXIST = "Email người dùng không tồn tại !";
    public static final String ACCOUNT_BLOCKED = "Tài khoản người dùng đã bị khoá !";
    public static final String ACCOUNT_NOT_ACTIVATED = "Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email !";
    public static final String ACCOUNT_ACTIVATED = "Tài khoản người dùng đã được kích hoạt !";
    public static final String WRONG_ACTIVATE_CODE = "Mã kích hoạt tài khoản không đúng !";
    public static final String WRONG_PASSWORD = "Sai tên đăng nhập hoặc mật khẩu";
    public static final String AUTHENTICATION_FAILED = "Xác thực thất bại";
    public static final String PASSWORD_DOES_NOT_MATCH ="Mật khẩu không trùng khớp";
    public static final String UPDATE_PASSWORD_FAILED ="Thay đổi mật khẩu thất bại";
    public static final String HAS_NOT_LOGIN = "Chưa đăng nhập";


    // JWT ERROR
    public static final String EXPIRED_TOKEN = "Token đã hết hạn, vui lòng đăng nhập lại";
    public static final String INVALID_TOKEN = "Token không hợp lệ";

}
