package com.cjq.androidx.tools;

import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect //必须使用@AspectJ标注，这样class DemoAspect就等同于 aspect DemoAspect了
public class DemoAspectJ {
    String TAG = "DemoAspectJ";

    /* @Pointcut：pointcut也变成了一个注解，这个注解是针对一个函数的，比如此处的logForActivity()
       其实它代表了这个pointcut的名字。如果是带参数的pointcut，则把参数类型和名字放到    代表pointcut名字的logForActivity中，然后在@Pointcut注解中使用参数名。
       基本和以前一样，只是写起来比较奇特一点。后面我们会介绍带参数的例子    */
    @Pointcut("execution(* com.cjq.androidx.activity.AopDemoActivity.onCreate(..)) ||" + "execution(* com.ml.maskpro.ui.MainActivity.onStart(..))")
    public void logForActivity() {
        // 声明所AopDemoActivity里面的onCreate方法和MainActivity.onStart()，会被拦截
    }  //注意，这个函数必须要有实现，否则Java编译器会报错

    @Pointcut("execution(* *..*+.on**(..))")//最主要是PointCut描述，它来决定哪些方法可以被拦截
    public void printLifecycleForActivity() {
        // 声明所有带on前缀的方法，都会拦截
    }  //注意，这个函数必须要有实现，否则Java编译器会报错

    /*    @Before：这就是Before的advice，对于after，after -returning，和after-throwing。
    对应的注解格式为    @After，@AfterReturning，@AfterThrowing。
    Before后面跟的是pointcut名字，然后其代码块由一个函数来实现。比如此处的log。
    */
    @Before("logForActivity()")
    public void log(JoinPoint joinPoint) {
        Log.e(TAG, joinPoint.toShortString() + " aop拦截");
    }

    @After("printLifecycleForActivity()")
    public void toastAop(JoinPoint joinPoint){
       // ToastUtils.showShort(joinPoint.toShortString() + " toastAop拦截");
    }
}
