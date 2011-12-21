package com.grahamlea.glissmetrics.exampleapplication;

import com.grahamlea.glissmetrics.exampleapplication.services.*;
import com.grahamlea.glissmetrics.metric.Metric;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExampleApplication {

    public static final int RUNNING_TIME_SECONDS = 16;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application.xml");

        doLotsOfAuthorisations(context);

        printMonitoringInfo(context);
    }

    private static void printMonitoringInfo(ClassPathXmlApplicationContext context) {
        System.out.println();
        for (Metric metric : context.getBeansOfType(Metric.class).values()) {
            System.out.println(metric);
        }
    }

    private static void doLotsOfAuthorisations(ClassPathXmlApplicationContext context) {
        AuthorisationRouter authorisationRouter = context.getBean(AuthorisationRouter.class);
//        doLotsOfAuthorisationsInThisThread(authorisationRouter);
        doLotsOfAuthorisationsInLotsOfThreads(authorisationRouter);
        try {
            Thread.sleep(RUNNING_TIME_SECONDS * 1000 + 2000);
        } catch (InterruptedException e) {
            // Ignored
        }
    }

    private static void doLotsOfAuthorisationsInLotsOfThreads(final AuthorisationRouter authorisationRouter) {
        final long endTime = System.currentTimeMillis() + RUNNING_TIME_SECONDS * 1000;
        int numberOfThreads = 50;
        System.out.format("Starting %s threads to call authorise() full-tilt for %s seconds\n", numberOfThreads, RUNNING_TIME_SECONDS);
        while (numberOfThreads-- > 0) {
            new Thread(new Runnable() {
                public void run() {
                    System.out.print(">");
                    String request = String.valueOf(Math.random());
                    while (System.currentTimeMillis() < endTime) {
                        try {
                            authorisationRouter.authorise(request);
                        } catch (NoServiceAvailableException e) {
                            // Ignored
                        } catch (TimedOutException e) {
                            // Ignored
                        }
                    }
                    System.out.print("<");
                }
            }).start();
        }
    }

    private static void doLotsOfAuthorisationsInThisThread(AuthorisationRouter authorisationRouter) {
        long endTime = System.currentTimeMillis() + RUNNING_TIME_SECONDS * 1000;
        int count = 0;
        String request = String.valueOf(Math.random());
        while (System.currentTimeMillis() < endTime) {
            try {
                AuthorisationResult result = authorisationRouter.authorise(request);
                System.out.print(result.getResultCode() == ResultCode.Approved ? '.' : 'x');
            } catch (NoServiceAvailableException e) {
                System.out.print('!');
            } catch (TimedOutException e) {
                System.out.print('-');
            }
            if (count++ % 80 == 0)
                System.out.println();
        }
    }
}
