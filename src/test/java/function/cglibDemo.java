package function;

import Tree_Span.BranchTreeRoot;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Collection;

public class cglibDemo {
    public void cglibtest(){
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(BranchTreeRoot.class);
        enhancer.setCallback(new MethodInterceptor(){

            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("begin");
                Object invoke = methodProxy.invoke(new  BranchTreeRoot(){
                    private String msg="aabb";
                    public String getMsg(){
                        return msg;
                    }
                    @Override
                    protected void addChild(BranchTreeRoot child) {

                    }

                    @Override
                    protected Collection<?> getChilds() {
                        return null;
                    }

                    @Override
                    public void SetAttribute(String attr, String o) {

                    }
                }, objects);
                System.out.println("end");
                return invoke;
            }
        });
        BranchTreeRoot btr= (BranchTreeRoot) enhancer.create();
//        btr.getMsg();
    }
}
