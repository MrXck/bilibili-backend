package com.bilibili.handler;

import com.bilibili.common.R;
import com.bilibili.exception.APIException;
import com.bilibili.exception.LoginException;
import com.bilibili.exception.SmsException;
import com.bilibili.exception.UserException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

import static com.bilibili.utils.Constant.FILE_NOT_FOUND;
import static com.bilibili.utils.Constant.LOGIN_ERROR;

/**
 * @author xck
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public R<String> handleBindException(IOException ex) {
        ex.printStackTrace();
        return R.error(FILE_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public R<String> handleBindException(Exception ex) {
        ex.printStackTrace();
        return R.error("操作失败");
    }

    @ExceptionHandler(LoginException.class)
    public R<String> handleBindException(LoginException ex) {
        return R.error(LOGIN_ERROR);
    }

    @ExceptionHandler(SmsException.class)
    public R<String> handleBindException(SmsException ex) {
        return R.error(ex.getMessage());
    }

    @ExceptionHandler(UserException.class)
    public R<String> handleBindException(UserException ex) {
        return R.error(ex.getMessage());
    }

    @ExceptionHandler(APIException.class)
    public R<String> handleBindException(APIException ex) {
        // TODO 日志
        return R.error(ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public R<String> handleBindException(BindException ex) {
        ObjectError objectError = ex.getBindingResult().getAllErrors().get(0);
        // TODO 日志
        return R.error(objectError.getDefaultMessage());
    }

}
