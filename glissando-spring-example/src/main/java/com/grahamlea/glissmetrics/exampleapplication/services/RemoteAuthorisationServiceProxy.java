package com.grahamlea.glissmetrics.exampleapplication.services;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RemoteAuthorisationServiceProxy implements RemoteService {

    { try { Thread.sleep(100); } catch (InterruptedException e) {} }
    private final Random random = new Random((long) (System.currentTimeMillis() * Math.random()));

    private double failureRate;
    private double timeoutRate;

    public AuthorisationResult authorise(String request) {
        if (random.nextDouble() < timeoutRate) {
            return null;
        }
        boolean decline = random.nextDouble() < (failureRate * (1 + timeoutRate));
        return new AuthorisationResult(decline ? ResultCode.Declined : ResultCode.Approved);
    }

    @Required
    public void setFailureRate(double failureRate) {
        this.failureRate = failureRate;
    }

    @Required
    public void setTimeoutRate(double timeoutRate) {
        this.timeoutRate = timeoutRate;
    }
}
