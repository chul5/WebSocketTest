package test.communication.util;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
@Aspect
@Component
@Slf4j


public class LoggingAspect {
	@Around("execution(* test.communication.controller.StompChatController.*(..))")
	public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		System.out.println("Start:" + joinPoint.toString());

		try{
			return joinPoint.proceed();
		}
		finally {
			long finish = System.currentTimeMillis();
			long timeMs = finish - start;

			System.out.println("End : " + joinPoint.toString() + " " + timeMs);
		}
	}

	@Around("@annotation(measureExecutionTime)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint, MeasureExecutionTime measureExecutionTime) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object proceed = joinPoint.proceed();
		long endTime = System.currentTimeMillis();
		System.out.println("{" + joinPoint.getSignature() + "}" + "executed in {" + (endTime - startTime) + "} ms");
		return proceed;
	}
}