package cn.pandacoder.gulimall.product.exception;

import cn.pandacoder.common.exception.BizCodeEnum;
import cn.pandacoder.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = {"cn.pandacoder.gulimall.product.controller"})
public class GuliMallExceptionControllerAdvice {


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
//        log.error("数据校验出现问题{},异常类型{}", e.getMessage(), e.getClass());
        BindingResult result = e.getBindingResult();

        Map<String, String> map = new HashMap<>();
        result.getFieldErrors().forEach((item) -> {
            //获取发生错误字段的错误信息
            String msg = item.getDefaultMessage();
            //获得发生错误的字段名
            String field = item.getField();
            map.put(field, msg);
        });
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data", map);
    }

/*    @ExceptionHandler(value = Throwable.class)
    public R handleUnknownException(Exception e) {
        log.error("出现未知异常,异常类型{}, 详细为{}, 原因{}", e.getMessage(), e.getClass(), e.getCause());
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION.getCode(), BizCodeEnum.UNKNOWN_EXCEPTION.getMsg());
    }*/
}
