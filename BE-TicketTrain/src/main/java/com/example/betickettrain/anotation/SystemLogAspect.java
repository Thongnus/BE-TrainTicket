package com.example.betickettrain.logging;

import com.example.betickettrain.anotation.LogAction;
import com.example.betickettrain.entity.SystemLog;
import com.example.betickettrain.entity.User;
import com.example.betickettrain.repository.SystemLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SystemLogAspect {

    private final HttpServletRequest request;
    private final SystemLogRepository systemLogRepository;

    @AfterReturning(pointcut = "@annotation(logAction)", returning = "result")
    public void logAction(JoinPoint joinPoint, LogAction logAction, Object result) {
        try {
            Long userId = getCurrentUserId();
       //     Object firstArg = joinPoint.getArgs()[0];
            Integer entityId = extractEntityId(result, joinPoint.getArgs());
            String desString = logAction.description() +"id:"+entityId;

           // String description = resolveDescription(logAction.description(), joinPoint);

            SystemLog logg = SystemLog.builder()
                    .user(userId != null ? User.builder().userId(userId).build() : null)
                    .action(logAction.action())
                    .entityType(logAction.entity())
                    .entityId(entityId)
                    .description(desString)
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .logTime(LocalDateTime.now())
                    .build();

            systemLogRepository.save(logg);
            log.debug("📘 Logged [{}] [{}:{}] - {}", logAction.action(), logAction.entity(), entityId, desString);
        } catch (Exception e) {
            log.warn("⚠️ Failed to log action: {}", e.getMessage());
        }
    }

    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User details) {
                return details.getUserId();
            }
        } catch (Exception ignored) {}
        return null;
    }
    // lấy id của entity
    private Integer extractEntityId(Object result, Object[] args) {
        // Ưu tiên lấy từ kết quả trả về sau khi save (đã có ID)
        Integer id = tryExtractId(result);
        if (id != null) return id;

        // Fallback: lấy từ tham số đầu tiên (thường là DTO)
        if (args.length > 0) {
            return tryExtractId(args[0]);
        }

        return null;
    }

    private Integer tryExtractId(Object obj) {
        try {
            if (obj == null) return null;
            if (obj instanceof Integer i) return i;

            for (var method : obj.getClass().getMethods()) {
                if (method.getName().startsWith("get") &&
                        method.getName().endsWith("Id") &&
                        method.getParameterCount() == 0 &&
                        (method.getReturnType() == Integer.class || method.getReturnType() == Long.class)) {

                    Object idValue = method.invoke(obj);
                    if (idValue != null) {
                        return ((Number) idValue).intValue();
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Không thể trích xuất ID từ {}", obj.getClass().getSimpleName());
        }
        return null;
    }



    private String resolveDescription(String expr, JoinPoint joinPoint) {
        if (!expr.contains("#{")) return expr;

        try {
            SpelExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = new StandardEvaluationContext();

            Object[] args = joinPoint.getArgs();
            String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }

            // 🔥 Đây là điểm quan trọng: dùng TemplateParserContext
            return parser.parseExpression(expr, new TemplateParserContext()).getValue(context, String.class);

        } catch (Exception e) {
            log.error("SpEL parse error for: {}", expr, e);
            return "[SpEL error]";
        }
    }

}
